package br.tec.dev2b.app.sala.controller;

import br.tec.dev2b.app.sala.dto.AtualizarSalaDto;
import br.tec.dev2b.app.sala.dto.CriarSalaDto;
import br.tec.dev2b.app.sala.dto.SalaDto;
import br.tec.dev2b.app.sala.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @PostMapping
    public ResponseEntity<SalaDto> criar(@RequestBody CriarSalaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<SalaDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(salaService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/ativas")
    public ResponseEntity<List<SalaDto>> listarAtivasPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(salaService.listarAtivasPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SalaDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarSalaDto dto) {
        return ResponseEntity.ok(salaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
