package br.tec.dev2b.app.dashboard.controller;

import br.tec.dev2b.app.dashboard.dto.DashboardDto;
import br.tec.dev2b.app.dashboard.service.DashboardService;
import br.tec.dev2b.app.dashboard.service.RelatorioDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;
    private final RelatorioDashboardService relatorioService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(service.getDashboard(empresaId));
    }

    @GetMapping(value = "/empresa/{empresaId}/relatorio", produces = "application/pdf")
    public ResponseEntity<byte[]> getRelatorio(
            @PathVariable UUID empresaId,
            @RequestParam(required = false) UUID usuarioId) throws IOException {
        byte[] pdf = relatorioService.gerarPdf(empresaId, usuarioId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=\"relatorio-dashboard.pdf\"")
                .body(pdf);
    }
}
