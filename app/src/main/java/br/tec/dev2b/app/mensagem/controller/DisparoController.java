package br.tec.dev2b.app.mensagem.controller;

import br.tec.dev2b.app.mensagem.service.DisparoAutomaticoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint para disparo manual / testes do job automático.
 * Útil para validar a integração sem esperar o cron.
 */
@RestController
@RequestMapping("/api/v1/disparos")
@RequiredArgsConstructor
public class DisparoController {

    private final DisparoAutomaticoService disparoAutomaticoService;

    /**
     * POST /api/v1/disparos/confirmacao
     * Executa imediatamente o job de confirmação de presença.
     */
    @PostMapping("/confirmacao")
    public ResponseEntity<Map<String, Object>> dispararConfirmacao() {
        int enviados = disparoAutomaticoService.executarDisparoConfirmacao();
        return ResponseEntity.ok(Map.of(
                "mensagem", "Disparo de confirmação executado",
                "mensagensEnviadas", enviados
        ));
    }

    /**
     * POST /api/v1/disparos/remarcacao
     * Executa imediatamente o job de remarcação.
     */
    @PostMapping("/remarcacao")
    public ResponseEntity<Map<String, Object>> dispararRemarcacao() {
        int enviados = disparoAutomaticoService.executarDisparoRemarcacao();
        return ResponseEntity.ok(Map.of(
                "mensagem", "Disparo de remarcação executado",
                "mensagensEnviadas", enviados
        ));
    }
}
