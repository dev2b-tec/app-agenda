package br.tec.dev2b.app.integracao.controller;

import br.tec.dev2b.app.integracao.dto.IntegracaoDto;
import br.tec.dev2b.app.integracao.dto.SalvarIntegracaoDto;
import br.tec.dev2b.app.integracao.service.IntegracaoService;
import br.tec.dev2b.app.integracao.service.SmtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integracoes")
@RequiredArgsConstructor
public class IntegracaoController {

    private final IntegracaoService integracaoService;
    private final SmtpService smtpService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<IntegracaoDto>> listar(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(integracaoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<IntegracaoDto> buscarPorTipo(
            @PathVariable UUID empresaId,
            @PathVariable String tipo) {
        IntegracaoDto dto = integracaoService.buscarPorTipo(empresaId, tipo);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();
    }

    @PutMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<IntegracaoDto> salvar(
            @PathVariable UUID empresaId,
            @PathVariable String tipo,
            @RequestBody SalvarIntegracaoDto dto) {
        return ResponseEntity.ok(integracaoService.salvar(empresaId, tipo, dto));
    }

    @DeleteMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID empresaId,
            @PathVariable String tipo) {
        integracaoService.deletar(empresaId, tipo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/empresa/{empresaId}/smtp/testar")
    public ResponseEntity<Void> testarSmtp(
            @PathVariable UUID empresaId,
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        smtpService.testar(empresaId, email);
        return ResponseEntity.ok().build();
    }
}
