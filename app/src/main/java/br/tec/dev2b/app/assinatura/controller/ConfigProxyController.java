package br.tec.dev2b.app.assinatura.controller;

import br.tec.dev2b.app.assinatura.service.AssinaturaIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigProxyController {

    private final AssinaturaIntegrationService integrationService;

    @GetMapping("/mp")
    public ResponseEntity<Map<String, Object>> mpConfig() {
        return ResponseEntity.ok(integrationService.buscarConfigMp());
    }
}
