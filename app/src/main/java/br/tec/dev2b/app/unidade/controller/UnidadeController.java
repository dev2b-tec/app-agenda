package br.tec.dev2b.app.unidade.controller;

import br.tec.dev2b.app.unidade.dto.AtualizarUnidadeDto;
import br.tec.dev2b.app.unidade.dto.CriarUnidadeDto;
import br.tec.dev2b.app.unidade.dto.UnidadeDto;
import br.tec.dev2b.app.unidade.service.UnidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService unidadeService;

    @PostMapping
    public ResponseEntity<UnidadeDto> criar(@RequestBody CriarUnidadeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UnidadeDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(unidadeService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/ativas")
    public ResponseEntity<List<UnidadeDto>> listarAtivasPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(unidadeService.listarAtivasPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadeDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UnidadeDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarUnidadeDto dto) {
        return ResponseEntity.ok(unidadeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        unidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
