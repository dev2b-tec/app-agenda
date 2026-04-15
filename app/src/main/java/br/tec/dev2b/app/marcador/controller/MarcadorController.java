package br.tec.dev2b.app.marcador.controller;

import br.tec.dev2b.app.marcador.dto.AtualizarMarcadorDto;
import br.tec.dev2b.app.marcador.dto.CriarMarcadorDto;
import br.tec.dev2b.app.marcador.dto.MarcadorDto;
import br.tec.dev2b.app.marcador.service.MarcadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/marcadores")
@RequiredArgsConstructor
public class MarcadorController {

    private final MarcadorService marcadorService;

    @PostMapping
    public ResponseEntity<MarcadorDto> criar(@RequestBody CriarMarcadorDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcadorService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<MarcadorDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(marcadorService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarcadorDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(marcadorService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MarcadorDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarMarcadorDto dto) {
        return ResponseEntity.ok(marcadorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        marcadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
