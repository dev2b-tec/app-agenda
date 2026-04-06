package br.tec.dev2b.app.siteconfig.controller;

import br.tec.dev2b.app.siteconfig.dto.*;
import br.tec.dev2b.app.siteconfig.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<SiteConfigDto> buscar(@PathVariable UUID usuarioId) {
        SiteConfigDto dto = siteConfigService.buscarPorUsuario(usuarioId);
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/usuario/{usuarioId}")
    public ResponseEntity<SiteConfigDto> salvar(
            @PathVariable UUID usuarioId,
            @RequestBody SalvarSiteConfigDto dto) {
        return ResponseEntity.ok(siteConfigService.salvar(usuarioId, dto));
    }

    @PostMapping("/usuario/{usuarioId}/blog")
    public ResponseEntity<BlogPostDto> criarPost(
            @PathVariable UUID usuarioId,
            @RequestBody CriarBlogPostDto dto) {
        return ResponseEntity.ok(siteConfigService.criarPost(usuarioId, dto));
    }

    @DeleteMapping("/blog/{postId}")
    public ResponseEntity<Void> deletarPost(@PathVariable UUID postId) {
        siteConfigService.deletarPost(postId);
        return ResponseEntity.noContent().build();
    }
}
