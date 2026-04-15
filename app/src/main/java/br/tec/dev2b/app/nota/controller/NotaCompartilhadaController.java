package br.tec.dev2b.app.nota.controller;

import br.tec.dev2b.app.nota.dto.CriarNotaDto;
import br.tec.dev2b.app.nota.dto.NotaCompartilhadaDto;
import br.tec.dev2b.app.nota.service.NotaCompartilhadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notas-compartilhadas")
@RequiredArgsConstructor
public class NotaCompartilhadaController {

    private final NotaCompartilhadaService notaService;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<NotaCompartilhadaDto>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(notaService.listarPorPaciente(pacienteId));
    }

    @PostMapping
    public ResponseEntity<NotaCompartilhadaDto> criar(@RequestBody CriarNotaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaService.criar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @RequestParam("autorId") UUID autorId) {
        notaService.deletar(id, autorId);
        return ResponseEntity.noContent().build();
    }
}
