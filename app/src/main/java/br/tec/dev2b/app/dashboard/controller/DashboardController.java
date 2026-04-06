package br.tec.dev2b.app.dashboard.controller;

import br.tec.dev2b.app.dashboard.dto.DashboardDto;
import br.tec.dev2b.app.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(service.getDashboard(empresaId));
    }
}
