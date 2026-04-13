package br.tec.dev2b.app.infra.whereby;

import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WherebyWebhookService {

    private final WhereBySessaoRepository sessaoRepository;
    private final AgendamentoRepository   agendamentoRepository;

    // ─── Cálculo de minutos (teto) ────────────────────────────────────────────
    // 1s → 1 min | 60s → 1 min | 61s → 2 min
    private static int calcularMinutos(long segundos) {
        if (segundos <= 0) return 1;
        return (int) Math.ceil(segundos / 60.0);
    }

    // ─── room.client.joined ───────────────────────────────────────────────────
    @Transactional
    public void handleJoined(Map<String, Object> data) {
        String meetingId     = stringOf(data, "meetingId");
        String participantId = stringOf(data, "participantId");
        String displayName   = stringOf(data, "displayName");
        String joinedAtStr   = stringOf(data, "joinedAt");

        if (meetingId == null || participantId == null) {
            log.warn("[Whereby Webhook] joined sem meetingId/participantId: {}", data);
            return;
        }

        // Idempotência: ignora se já existe sessão aberta para este participante
        if (sessaoRepository.findByMeetingIdAndParticipantIdAndEndedAtIsNull(meetingId, participantId).isPresent()) {
            log.info("[Whereby Webhook] sessão já registrada — meetingId={} participantId={}", meetingId, participantId);
            return;
        }

        LocalDateTime startedAt = joinedAtStr != null
                ? LocalDateTime.parse(joinedAtStr.replace("Z", "").replaceAll("\\.\\d+$", ""))
                : LocalDateTime.now();

        // Tenta associar ao agendamento pelo meetingId
        var agendamento = agendamentoRepository.findByWherebyMeetingId(meetingId).orElse(null);

        WhereBySessao sessao = WhereBySessao.builder()
                .meetingId(meetingId)
                .participantId(participantId)
                .participantName(displayName)
                .startedAt(startedAt)
                .agendamentoId(agendamento != null ? agendamento.getId() : null)
                .build();

        sessaoRepository.save(sessao);
        log.info("[Whereby Webhook] participante entrou — meetingId={} participant={}", meetingId, displayName);
    }

    // ─── room.client.left ─────────────────────────────────────────────────────
    @Transactional
    public void handleLeft(Map<String, Object> data) {
        String meetingId        = stringOf(data, "meetingId");
        String participantId    = stringOf(data, "participantId");
        String leftAtStr        = stringOf(data, "leftAt");
        Object durationSecondsRaw = data.get("durationSeconds");

        if (meetingId == null || participantId == null) {
            log.warn("[Whereby Webhook] left sem meetingId/participantId: {}", data);
            return;
        }

        var sessaoOpt = sessaoRepository.findByMeetingIdAndParticipantIdAndEndedAtIsNull(meetingId, participantId);
        if (sessaoOpt.isEmpty()) {
            log.warn("[Whereby Webhook] sessão aberta não encontrada — meetingId={} participantId={}", meetingId, participantId);
            return;
        }

        WhereBySessao sessao = sessaoOpt.get();

        LocalDateTime endedAt = leftAtStr != null
                ? LocalDateTime.parse(leftAtStr.replace("Z", "").replaceAll("\\.\\d+$", ""))
                : LocalDateTime.now();

        long durationSeconds = durationSecondsRaw instanceof Number n ? n.longValue() : 0L;
        if (durationSeconds <= 0) {
            // Calcula pela diferença se o Whereby não enviou o campo
            durationSeconds = java.time.Duration.between(sessao.getStartedAt(), endedAt).getSeconds();
        }

        sessao.setEndedAt(endedAt);
        sessao.setDuracaoMinutos(calcularMinutos(durationSeconds));
        sessaoRepository.save(sessao);

        log.info("[Whereby Webhook] participante saiu — meetingId={} participant={} duração={}min",
                meetingId, sessao.getParticipantName(), sessao.getDuracaoMinutos());
    }

    // ─── Job de resiliência: encerra sessões órfãs ────────────────────────────
    // Roda a cada hora. Encerra sessões abertas cujo agendamento terminou há > 2h.
    @Scheduled(fixedDelay = 3_600_000)
    @Transactional
    public void encerrarSessoesOrfas() {
        LocalDateTime limite = LocalDateTime.now().minusHours(2);
        List<WhereBySessao> orfas = sessaoRepository.findOrfas(limite);

        if (orfas.isEmpty()) return;

        log.warn("[Whereby Job] encerrando {} sessão(ões) órfã(s)", orfas.size());
        for (WhereBySessao s : orfas) {
            // Usa fim do agendamento como endedAt estimado
            var agendamento = s.getAgendamentoId() != null
                    ? agendamentoRepository.findById(s.getAgendamentoId()).orElse(null)
                    : null;

            LocalDateTime endedAt = (agendamento != null) ? agendamento.getFim() : limite;
            long durSecs = java.time.Duration.between(s.getStartedAt(), endedAt).getSeconds();

            s.setEndedAt(endedAt);
            s.setDuracaoMinutos(calcularMinutos(Math.max(durSecs, 0)));
            sessaoRepository.save(s);

            log.warn("[Whereby Job] sessão encerrada por timeout — id={} participant={} duração={}min",
                    s.getId(), s.getParticipantName(), s.getDuracaoMinutos());
        }
    }

    // ─── helper ───────────────────────────────────────────────────────────────
    private static String stringOf(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}
