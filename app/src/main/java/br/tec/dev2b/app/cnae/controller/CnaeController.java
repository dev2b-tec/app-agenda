package br.tec.dev2b.app.cnae.controller;

import br.tec.dev2b.app.cnae.dto.AtualizarCnaeDto;
import br.tec.dev2b.app.cnae.dto.CnaeDto;
import br.tec.dev2b.app.cnae.dto.CriarCnaeDto;
import br.tec.dev2b.app.cnae.service.CnaeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cnae")
@RequiredArgsConstructor
public class CnaeController {

    private final CnaeService cnaeService;

    @PostMapping
    public ResponseEntity<CnaeDto> criar(@RequestBody CriarCnaeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cnaeService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<CnaeDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(cnaeService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/ativos")
    public ResponseEntity<List<CnaeDto>> listarAtivosPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(cnaeService.listarAtivosPorEmpresa(empresaId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CnaeDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarCnaeDto dto) {
        return ResponseEntity.ok(cnaeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        cnaeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
