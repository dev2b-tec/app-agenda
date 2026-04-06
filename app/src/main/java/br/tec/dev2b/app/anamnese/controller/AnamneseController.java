package br.tec.dev2b.app.anamnese.controller;

import br.tec.dev2b.app.anamnese.dto.AnamneseDto;
import br.tec.dev2b.app.anamnese.dto.AtualizarAnamneseDto;
import br.tec.dev2b.app.anamnese.dto.CriarAnamneseDto;
import br.tec.dev2b.app.anamnese.service.AnamneseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/anamneses")
@RequiredArgsConstructor
public class AnamneseController {

    private final AnamneseService anamneseService;

    @PostMapping
    public ResponseEntity<AnamneseDto> criar(@RequestBody CriarAnamneseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anamneseService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<AnamneseDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(anamneseService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnamneseDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(anamneseService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnamneseDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarAnamneseDto dto) {
        return ResponseEntity.ok(anamneseService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        anamneseService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
