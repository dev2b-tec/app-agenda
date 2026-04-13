package br.tec.dev2b.app.infra.whereby;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/whereby")
@RequiredArgsConstructor
@Slf4j
public class WherebyWebhookController {

    private final WherebyWebhookService webhookService;

    /**
     * Recebe eventos do Whereby:
     *  - room.client.joined
     *  - room.client.left
     *
     * Configurar no painel: https://dev-2-b.whereby.com/org/settings/webhooks
     * URL: https://<seu-dominio>/api/v1/whereby/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        String type = payload.get("type") instanceof String s ? s : "";
        @SuppressWarnings("unchecked")
        Map<String, Object> data = payload.get("data") instanceof Map<?, ?> m
                ? (Map<String, Object>) m
                : payload; // alguns eventos enviam os campos no root

        log.info("[Whereby Webhook] tipo={}", type);

        switch (type) {
            case "room.client.joined"  -> webhookService.handleJoined(data);
            case "room.client.left"    -> webhookService.handleLeft(data);
            default -> log.debug("[Whereby Webhook] evento ignorado: {}", type);
        }

        // Whereby espera 200 para não fazer retry
        return ResponseEntity.ok().build();
    }
}
