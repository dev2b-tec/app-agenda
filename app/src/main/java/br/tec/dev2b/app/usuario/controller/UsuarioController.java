package br.tec.dev2b.app.usuario.controller;

import br.tec.dev2b.app.usuario.dto.AtualizarUsuarioDto;
import br.tec.dev2b.app.usuario.dto.CriarUsuarioDto;
import br.tec.dev2b.app.usuario.dto.SyncUsuarioDto;
import br.tec.dev2b.app.usuario.dto.UsuarioDto;
import br.tec.dev2b.app.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDto> criar(@RequestBody CriarUsuarioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    @PostMapping("/sync")
    public ResponseEntity<UsuarioDto> sync(@RequestBody SyncUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.sync(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<UsuarioDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(usuarioService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDto> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UsuarioDto> buscarPorKeycloakId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(usuarioService.buscarPorKeycloakId(keycloakId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarUsuarioDto dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("[POST /{}/foto] contentType={} size={}", id, file.getContentType(), file.getSize());
        String url = usuarioService.uploadFoto(id, file);
        log.info("[POST /{}/foto] concluído, url={}", id, url);
        return ResponseEntity.ok(Map.of("fotoUrl", url));
    }

    @GetMapping("/{id}/foto-url")
    public ResponseEntity<Map<String, String>> obterFotoUrl(@PathVariable UUID id) {
        log.info("[GET /{}/foto-url] solicitado", id);
        String url = usuarioService.obterFotoUrl(id);
        log.info("[GET /{}/foto-url] url gerada={}", id, url);
        return ResponseEntity.ok(Map.of("fotoUrl", url));
    }

    @PostMapping(value = "/{id}/assinatura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadAssinatura(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        String url = usuarioService.uploadAssinatura(id, file);
        return ResponseEntity.ok(Map.of("assinaturaUrl", url));
    }

    @PatchMapping("/{id}/agenda-profissional")
    public ResponseEntity<UsuarioDto> habilitarAgendaProfissional(
            @PathVariable UUID id,
            @RequestBody Map<String, Boolean> body) {
        boolean habilitar = body.getOrDefault("habilitar", false);
        return ResponseEntity.ok(usuarioService.habilitarAgendaProfissional(id, habilitar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
