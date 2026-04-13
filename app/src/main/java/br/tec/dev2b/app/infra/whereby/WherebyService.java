package br.tec.dev2b.app.infra.whereby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WherebyService {

    @Value("${whereby.api-key:}")
    private String apiKey;

    @Value("${whereby.api-url:https://api.whereby.dev}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public record SalaResult(String meetingId, String hostRoomUrl, String viewerRoomUrl) {}

    /**
     * Cria uma sala Whereby disponível apenas entre {@code inicio} e {@code fim}.
     * Retorna null sem lançar exceção caso a integração esteja desabilitada ou falhe.
     */
    public SalaResult criarSala(LocalDateTime inicio, LocalDateTime fim) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[Whereby] API key não configurada — sala não criada");
            return null;
        }
        try {
            DateTimeFormatter iso = DateTimeFormatter.ISO_INSTANT;
            ZoneId br = ZoneId.of("America/Sao_Paulo");

            String startDate = inicio.atZone(br).withZoneSameInstant(ZoneOffset.UTC).format(iso);
            String endDate   = fim.atZone(br).withZoneSameInstant(ZoneOffset.UTC).format(iso);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "startDate", startDate,
                    "endDate", endDate,
                    "fields", List.of("hostRoomUrl")
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<WherebyMeetingResponse> response = restTemplate.postForEntity(
                    apiUrl + "/v1/meetings", entity, WherebyMeetingResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                WherebyMeetingResponse r = response.getBody();
                log.info("[Whereby] Sala criada: meetingId={}", r.getMeetingId());
                return new SalaResult(r.getMeetingId(), r.getHostRoomUrl(), r.getRoomUrl());
            }

            log.warn("[Whereby] Resposta inesperada ao criar sala: status={}", response.getStatusCode());
            return null;

        } catch (Exception e) {
            log.error("[Whereby] Erro ao criar sala: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Encerra a sala Whereby. Falhas são apenas logadas.
     */
    public void deletarSala(String meetingId) {
        if (apiKey == null || apiKey.isBlank() || meetingId == null || meetingId.isBlank()) return;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    apiUrl + "/v1/meetings/" + meetingId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
            log.info("[Whereby] Sala encerrada: meetingId={}", meetingId);
        } catch (Exception e) {
            log.error("[Whereby] Erro ao encerrar sala {}: {}", meetingId, e.getMessage());
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WherebyMeetingResponse {
        @JsonProperty("meetingId")
        private String meetingId;

        @JsonProperty("roomUrl")
        private String roomUrl;

        @JsonProperty("hostRoomUrl")
        private String hostRoomUrl;
    }
}
