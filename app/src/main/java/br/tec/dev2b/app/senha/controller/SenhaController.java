package br.tec.dev2b.app.senha.controller;

import br.tec.dev2b.app.senha.dto.AtualizarSenhaDto;
import br.tec.dev2b.app.senha.dto.CriarSenhaDto;
import br.tec.dev2b.app.senha.dto.SenhaDto;
import br.tec.dev2b.app.senha.service.SenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/senhas")
@RequiredArgsConstructor
public class SenhaController {

    private final SenhaService senhaService;

    @PostMapping
    public ResponseEntity<SenhaDto> criar(@RequestBody CriarSenhaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(senhaService.criar(dto));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SenhaDto>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(senhaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<SenhaDto>> listarPorUsuarioETipo(
            @PathVariable UUID usuarioId,
            @PathVariable String tipo) {
        return ResponseEntity.ok(senhaService.listarPorUsuarioETipo(usuarioId, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SenhaDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(senhaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SenhaDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarSenhaDto dto) {
        return ResponseEntity.ok(senhaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        senhaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
