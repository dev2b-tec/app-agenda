package br.tec.dev2b.app.mensagem.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Cliente HTTP para o app-integration-watts.
 * Chama GET /api/v1/instancias/empresa/{empresaId} para obter instâncias
 * e POST /api/v1/mensagens/enviar para enviar texto WhatsApp.
 */
@Slf4j
@Component
public class WhatsAppClient {

    private final RestClient restClient;

    public WhatsAppClient(@Value("${whats.service.url:http://localhost:8012}") String whatsUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(whatsUrl)
                .build();
    }

    /**
     * Retorna o id da primeira instância CONECTADA da empresa, ou null se não houver.
     */
    @SuppressWarnings("unchecked")
    public UUID buscarInstanciaConectada(UUID empresaId) {
        try {
            List<Map<String, Object>> instancias = restClient.get()
                    .uri("/api/v1/instancias/empresa/{id}", empresaId)
                    .retrieve()
                    .body(List.class);

            if (instancias == null || instancias.isEmpty()) return null;

            return instancias.stream()
                    .filter(i -> "CONECTADA".equalsIgnoreCase(String.valueOf(i.get("status"))))
                    .map(i -> UUID.fromString(String.valueOf(i.get("id"))))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Erro ao buscar instância WhatsApp para empresa {}: {}", empresaId, e.getMessage());
            return null;
        }
    }

    /**
     * Envia uma mensagem de texto via WhatsApp.
     *
     * @param instanciaId id da instância Evolution API
     * @param numero      número no formato internacional sem '+', ex: 5581999990000
     * @param texto       texto da mensagem
     * @return true se enviado com sucesso
     */
    public boolean enviarTexto(UUID instanciaId, String numero, String texto) {
        try {
            Map<String, Object> body = Map.of(
                    "instanciaId", instanciaId.toString(),
                    "numero", numero,
                    "tipo", "TEXTO",
                    "texto", texto
            );

            restClient.post()
                    .uri("/api/v1/mensagens/enviar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            return true;
        } catch (Exception e) {
            log.error("Erro ao enviar WhatsApp para {}: {}", numero, e.getMessage());
            return false;
        }
    }
}
