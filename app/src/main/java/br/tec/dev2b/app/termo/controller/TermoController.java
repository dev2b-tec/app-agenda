package br.tec.dev2b.app.termo.controller;

import br.tec.dev2b.app.termo.dto.AceitarTermoDto;
import br.tec.dev2b.app.termo.dto.TermoDto;
import br.tec.dev2b.app.termo.service.TermoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/termos")
@RequiredArgsConstructor
public class TermoController {

    private final TermoService termoService;

    @GetMapping("/{tipo}")
    public ResponseEntity<TermoDto> buscarTermoPorTipo(
            @PathVariable String tipo,
            @RequestParam UUID empresaId) {
        TermoDto termo = termoService.buscarTermoPorTipo(tipo, empresaId);
        return ResponseEntity.ok(termo);
    }

    @PostMapping("/aceitar")
    public ResponseEntity<Void> aceitarTermo(
            @RequestBody AceitarTermoDto dto,
            HttpServletRequest request) {
        termoService.aceitarTermo(dto, request);
        return ResponseEntity.ok().build();
    }
}
