package br.tec.dev2b.app.assinatura.controller;

import br.tec.dev2b.app.assinatura.service.AssinaturaIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Proxy de assinatura — encaminha as requisições do front-services
 * para o app-integration-mercado-pago via RestTemplate.
 */
@RestController
@RequestMapping("/api/v1/assinaturas")
@RequiredArgsConstructor
public class AssinaturaProxyController {

    private final AssinaturaIntegrationService integrationService;

    @GetMapping("/planos")
    public ResponseEntity<List<Map<String, Object>>> listarPlanos() {
        return ResponseEntity.ok(integrationService.listarPlanos());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarAssinatura(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(integrationService.criarAssinatura(dto));
    }

    @PostMapping("/pix")
    public ResponseEntity<Map<String, Object>> criarPix(@RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(integrationService.criarPagamentoPix(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Map<String, Object>>> listarPorEmpresa(@PathVariable String empresaId) {
        return ResponseEntity.ok(integrationService.listarAssinaturasPorEmpresa(empresaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(integrationService.cancelarAssinatura(id));
    }

    @PostMapping("/{id}/upgrade")
    public ResponseEntity<Map<String, Object>> upgrade(
            @PathVariable String id,
            @RequestBody Map<String, Object> dto) {
        return ResponseEntity.ok(integrationService.upgradeAssinatura(id, dto));
    }
}
