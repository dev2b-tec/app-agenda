package br.tec.dev2b.app.servico.controller;

import br.tec.dev2b.app.servico.dto.AtualizarServicoDto;
import br.tec.dev2b.app.servico.dto.CriarServicoDto;
import br.tec.dev2b.app.servico.dto.ServicoDto;
import br.tec.dev2b.app.servico.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    public ResponseEntity<ServicoDto> criar(@RequestBody CriarServicoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ServicoDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(servicoService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/ativos")
    public ResponseEntity<List<ServicoDto>> listarAtivosPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(servicoService.listarAtivosPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<List<ServicoDto>> listarPorEmpresaETipo(
            @PathVariable UUID empresaId,
            @PathVariable String tipo) {
        return ResponseEntity.ok(servicoService.listarPorEmpresaETipo(empresaId, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServicoDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarServicoDto dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
