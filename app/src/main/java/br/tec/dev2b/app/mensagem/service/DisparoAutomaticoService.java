package br.tec.dev2b.app.mensagem.service;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import br.tec.dev2b.app.agendamento.repository.AgendamentoRepository;
import br.tec.dev2b.app.mensagem.infra.WhatsAppClient;
import br.tec.dev2b.app.mensagem.model.MensagemPadrao;
import br.tec.dev2b.app.mensagem.model.NotificacaoAgendamento;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import br.tec.dev2b.app.mensagem.repository.ConfiguracaoMensagemRepository;
import br.tec.dev2b.app.mensagem.repository.MensagemPadraoRepository;
import br.tec.dev2b.app.mensagem.repository.NotificacaoAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo disparo automático de mensagens WhatsApp
 * para agendamentos futuros (confirmação de presença).
 *
 * <p>Regras:</p>
 * <ul>
 *   <li>Executa uma vez ao dia via {@link DisparoAutomaticoScheduler}</li>
 *   <li>Envia apenas se o profissional tem {@code envioSmsAutomatico = true}</li>
 *   <li>Envia apenas para agendamentos nos próximos {@code JANELA_DIAS} dias</li>
 *   <li>Cada tipo de mensagem é enviado no máximo uma vez por agendamento
 *       (controlado por {@code notificacoes_agendamento})</li>
 *   <li>Não envia para agendamentos com status Cancelado, Atendido ou Faltou</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisparoAutomaticoService {

    /** Janela de envio: agendamentos que ocorrerão nos próximos N dias (inclusive hoje) */
    private static final int JANELA_DIAS = 2;

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final AgendamentoRepository agendamentoRepository;
    private final ConfiguracaoMensagemRepository configuracaoMensagemRepository;
    private final MensagemPadraoRepository mensagemPadraoRepository;
    private final NotificacaoAgendamentoRepository notificacaoRepository;
    private final WhatsAppClient whatsAppClient;

    /**
     * Ponto de entrada do job diário — processa confirmações de presença.
     *
     * @return quantidade de mensagens enviadas com sucesso
     */
    @Transactional
    public int executarDisparoConfirmacao() {
        log.info("[DISPARO] Iniciando job de confirmação automática...");

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime janela = agora.plusDays(JANELA_DIAS).toLocalDate().atTime(23, 59, 59);

        // Busca todos os agendamentos na janela que não estejam cancelados/finalizados
        List<Agendamento> candidatos = agendamentoRepository
                .findAgendamentosParaDisparo(agora, janela);

        log.info("[DISPARO] {} agendamentos candidatos encontrados", candidatos.size());

        int enviados = 0;

        for (Agendamento agendamento : candidatos) {
            try {
                if (processarConfirmacao(agendamento)) enviados++;
            } catch (Exception e) {
                log.error("[DISPARO] Erro ao processar agendamento {}: {}", agendamento.getId(), e.getMessage());
            }
        }

        log.info("[DISPARO] Concluído — {} mensagens enviadas", enviados);
        return enviados;
    }

    /**
     * Ponto de entrada do job diário — processa remarcações.
     * Regra: apenas o agendamento mais recente (passado, pendente) por combinação paciente+profissional recebe a mensagem.
     *
     * @return quantidade de mensagens enviadas com sucesso
     */
    @Transactional
    public int executarDisparoRemarcacao() {
        log.info("[DISPARO] Iniciando job de remarcação automática...");

        LocalDateTime agora = LocalDateTime.now();

        List<Agendamento> candidatos = agendamentoRepository
                .findAgendamentosParaRemarcacao(agora);

        log.info("[DISPARO-REMARCACAO] {} agendamentos candidatos encontrados", candidatos.size());

        // Mantém apenas o mais recente por paciente+profissional
        java.util.Map<String, Agendamento> mapaUltimo = new java.util.LinkedHashMap<>();
        for (Agendamento ag : candidatos) {
            String pacienteId    = ag.getPaciente()  != null ? ag.getPaciente().getId().toString()  : "sem-paciente";
            String profissionalId = ag.getUsuario() != null ? ag.getUsuario().getId().toString() : "sem-profissional";
            String chave = pacienteId + "|" + profissionalId;
            // candidatos já estão ordenados DESC por inicio, então o primeiro encontrado é o mais recente
            mapaUltimo.putIfAbsent(chave, ag);
        }

        int enviados = 0;
        for (Agendamento agendamento : mapaUltimo.values()) {
            try {
                if (processarRemarcacao(agendamento)) enviados++;
            } catch (Exception e) {
                log.error("[DISPARO-REMARCACAO] Erro ao processar agendamento {}: {}", agendamento.getId(), e.getMessage());
            }
        }

        log.info("[DISPARO-REMARCACAO] Concluído — {} mensagens enviadas", enviados);
        return enviados;
    }

    // ─── private ────────────────────────────────────────────────────────────

    private boolean processarConfirmacao(Agendamento agendamento) {
        UUID agendamentoId = agendamento.getId();

        // 1. Já enviou confirmação para este agendamento?
        if (notificacaoRepository.existsByAgendamentoIdAndTipo(agendamentoId, TipoMensagemPadrao.CONFIRMAR_AGENDAMENTO)) {
            log.debug("[DISPARO] Confirmação já enviada para agendamento {}", agendamentoId);
            return false;
        }

        // 2. O profissional tem disparo automático ativado?
        if (agendamento.getUsuario() == null) return false;
        UUID usuarioId = agendamento.getUsuario().getId();

        boolean disparoAtivo = configuracaoMensagemRepository
                .findByUsuarioId(usuarioId)
                .map(c -> Boolean.TRUE.equals(c.getEnvioSmsAutomatico()))
                .orElse(false);

        if (!disparoAtivo) {
            log.debug("[DISPARO] Disparo automático desativado para usuário {}", usuarioId);
            return false;
        }

        // 3. Paciente tem telefone?
        String telefone = resolverTelefone(agendamento);
        if (telefone == null || telefone.isBlank()) {
            log.debug("[DISPARO] Paciente sem telefone no agendamento {}", agendamentoId);
            return false;
        }

        // 4. Empresa tem instância WhatsApp conectada?
        UUID empresaId = agendamento.getEmpresa().getId();
        UUID instanciaId = whatsAppClient.buscarInstanciaConectada(empresaId);
        if (instanciaId == null) {
            log.debug("[DISPARO] Nenhuma instância WhatsApp conectada para empresa {}", empresaId);
            return false;
        }

        // 5. Montar mensagem
        String texto = montar(agendamento, empresaId);

        // 6. Enviar
        String numeroFormatado = formatarNumero(telefone);
        boolean ok = whatsAppClient.enviarTexto(instanciaId, numeroFormatado, texto);

        if (ok) {
            // 7. Registrar para não reenviar
            notificacaoRepository.save(NotificacaoAgendamento.builder()
                    .agendamento(agendamento)
                    .tipo(TipoMensagemPadrao.CONFIRMAR_AGENDAMENTO)
                    .numeroDestino(numeroFormatado)
                    .build());

            log.info("[DISPARO] Confirmação enviada — agendamento={} paciente={} numero={}",
                    agendamentoId, agendamento.getPacienteNome(), numeroFormatado);
        }

        return ok;
    }

    private boolean processarRemarcacao(Agendamento agendamento) {
        UUID agendamentoId = agendamento.getId();

        // 1. Já enviou remarcação para este agendamento?
        if (notificacaoRepository.existsByAgendamentoIdAndTipo(agendamentoId, TipoMensagemPadrao.REMARCACAO)) {
            log.debug("[DISPARO-REMARCACAO] Remarcação já enviada para agendamento {}", agendamentoId);
            return false;
        }

        // 2. O profissional tem disparo automático ativado?
        if (agendamento.getUsuario() == null) return false;
        UUID usuarioId = agendamento.getUsuario().getId();

        boolean disparoAtivo = configuracaoMensagemRepository
                .findByUsuarioId(usuarioId)
                .map(c -> Boolean.TRUE.equals(c.getEnvioSmsAutomatico()))
                .orElse(false);

        if (!disparoAtivo) {
            log.debug("[DISPARO-REMARCACAO] Disparo automático desativado para usuário {}", usuarioId);
            return false;
        }

        // 3. Paciente tem telefone?
        String telefone = resolverTelefone(agendamento);
        if (telefone == null || telefone.isBlank()) {
            log.debug("[DISPARO-REMARCACAO] Paciente sem telefone no agendamento {}", agendamentoId);
            return false;
        }

        // 4. Empresa tem instância WhatsApp conectada?
        UUID empresaId = agendamento.getEmpresa().getId();
        UUID instanciaId = whatsAppClient.buscarInstanciaConectada(empresaId);
        if (instanciaId == null) {
            log.debug("[DISPARO-REMARCACAO] Nenhuma instância WhatsApp conectada para empresa {}", empresaId);
            return false;
        }

        // 5. Montar mensagem
        String texto = montarRemarcacao(agendamento, empresaId);

        // 6. Enviar
        String numeroFormatado = formatarNumero(telefone);
        boolean ok = whatsAppClient.enviarTexto(instanciaId, numeroFormatado, texto);

        if (ok) {
            notificacaoRepository.save(NotificacaoAgendamento.builder()
                    .agendamento(agendamento)
                    .tipo(TipoMensagemPadrao.REMARCACAO)
                    .numeroDestino(numeroFormatado)
                    .build());

            log.info("[DISPARO-REMARCACAO] Remarcação enviada — agendamento={} paciente={} numero={}",
                    agendamentoId, agendamento.getPacienteNome(), numeroFormatado);
        }

        return ok;
    }

    private String montarRemarcacao(Agendamento agendamento, UUID empresaId) {
        MensagemPadrao template = mensagemPadraoRepository
                .findByEmpresaIdAndTipo(empresaId, TipoMensagemPadrao.REMARCACAO)
                .orElseGet(() -> mensagemPadraoRepository
                        .findByEmpresaIdIsNullAndTipo(TipoMensagemPadrao.REMARCACAO)
                        .orElse(null));

        String texto = template != null ? template.getTexto()
                : "Olá #nome_paciente#! Você gostaria de reagendar uma consulta com #nome_profissional#, já que não compareceu em seu último agendamento?";

        String nomePaciente     = agendamento.getPacienteNome() != null ? agendamento.getPacienteNome() : "Paciente";
        String nomeProfissional = resolverNomeProfissional(agendamento);
        String dataHora         = agendamento.getInicio().format(DT_FMT);

        return texto
                .replace("#nome_paciente#", nomePaciente)
                .replace("#nome_profissional#", nomeProfissional)
                .replace("#data_e_hora_agendamento#", dataHora)
                .replace("#link_de_confirmacao#", "");
    }

    private String montar(Agendamento agendamento, UUID empresaId) {
        // Busca template da empresa ou o global
        MensagemPadrao template = mensagemPadraoRepository
                .findByEmpresaIdAndTipo(empresaId, TipoMensagemPadrao.CONFIRMAR_AGENDAMENTO)
                .orElseGet(() -> mensagemPadraoRepository
                        .findByEmpresaIdIsNullAndTipo(TipoMensagemPadrao.CONFIRMAR_AGENDAMENTO)
                        .orElse(null));

        String texto = template != null ? template.getTexto()
                : "Olá, #nome_paciente#! Sua consulta com o(a) #nome_profissional# está confirmada para #data_e_hora_agendamento#. Até lá!";

        String nomePaciente    = agendamento.getPacienteNome() != null ? agendamento.getPacienteNome() : "Paciente";
        String nomeProfissional = resolverNomeProfissional(agendamento);
        String dataHora        = agendamento.getInicio().format(DT_FMT);

        return texto
                .replace("#nome_paciente#", nomePaciente)
                .replace("#nome_profissional#", nomeProfissional)
                .replace("#data_e_hora_agendamento#", dataHora)
                .replace("#link_de_confirmacao#", "");
    }

    private String resolverTelefone(Agendamento agendamento) {
        if (agendamento.getPaciente() != null && agendamento.getPaciente().getTelefone() != null) {
            return agendamento.getPaciente().getTelefone();
        }
        return null;
    }

    private String resolverNomeProfissional(Agendamento agendamento) {
        if (agendamento.getUsuarioNome() != null) return agendamento.getUsuarioNome();
        if (agendamento.getUsuario() != null) return agendamento.getUsuario().getNome();
        return "profissional";
    }

    /** Remove qualquer não-dígito e garante prefixo 55 (Brasil) */
    private String formatarNumero(String telefone) {
        String digits = telefone.replaceAll("\\D", "");
        if (!digits.startsWith("55")) digits = "55" + digits;
        return digits;
    }
}
