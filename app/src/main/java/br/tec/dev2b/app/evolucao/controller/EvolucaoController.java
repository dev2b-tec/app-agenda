package br.tec.dev2b.app.evolucao.controller;

import br.tec.dev2b.app.evolucao.dto.AtualizarEvolucaoDto;
import br.tec.dev2b.app.evolucao.dto.CriarEvolucaoDto;
import br.tec.dev2b.app.evolucao.dto.EvolucaoDto;
import br.tec.dev2b.app.evolucao.service.EvolucaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evolucoes")
@RequiredArgsConstructor
public class EvolucaoController {

    private final EvolucaoService evolucaoService;

    @PostMapping
    public ResponseEntity<EvolucaoDto> criar(@RequestBody CriarEvolucaoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evolucaoService.criar(dto));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<EvolucaoDto>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(evolucaoService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvolucaoDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(evolucaoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EvolucaoDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarEvolucaoDto dto) {
        return ResponseEntity.ok(evolucaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        evolucaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
