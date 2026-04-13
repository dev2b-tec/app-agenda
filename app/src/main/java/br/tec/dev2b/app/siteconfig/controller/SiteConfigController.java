package br.tec.dev2b.app.siteconfig.controller;

import br.tec.dev2b.app.siteconfig.dto.*;
import br.tec.dev2b.app.siteconfig.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    // ── Private (authenticated) ────────────────────────────────────────────────

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

    @PutMapping("/blog/{postId}")
    public ResponseEntity<BlogPostDto> atualizarPost(
            @PathVariable UUID postId,
            @RequestBody AtualizarBlogPostDto dto) {
        return ResponseEntity.ok(siteConfigService.atualizarPost(postId, dto));
    }

    @DeleteMapping("/blog/{postId}")
    public ResponseEntity<Void> deletarPost(@PathVariable UUID postId) {
        siteConfigService.deletarPost(postId);
        return ResponseEntity.noContent().build();
    }

    // ── Image uploads ─────────────────────────────────────────────────────────

    @PostMapping(value = "/usuario/{usuarioId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadLogo(
            @PathVariable UUID usuarioId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of("url", siteConfigService.uploadLogo(usuarioId, file)));
    }

    @GetMapping("/usuario/{usuarioId}/logo-url")
    public ResponseEntity<Map<String, String>> logoUrl(@PathVariable UUID usuarioId) {
        String url = siteConfigService.obterLogoUrl(usuarioId);
        if (url == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping(value = "/usuario/{usuarioId}/perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadPerfil(
            @PathVariable UUID usuarioId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of("url", siteConfigService.uploadPerfil(usuarioId, file)));
    }

    @GetMapping("/usuario/{usuarioId}/perfil-url")
    public ResponseEntity<Map<String, String>> perfilUrl(@PathVariable UUID usuarioId) {
        String url = siteConfigService.obterPerfilUrl(usuarioId);
        if (url == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping(value = "/usuario/{usuarioId}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadBanner(
            @PathVariable UUID usuarioId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of("url", siteConfigService.uploadBanner(usuarioId, file)));
    }

    @GetMapping("/usuario/{usuarioId}/banner-url")
    public ResponseEntity<Map<String, String>> bannerUrl(@PathVariable UUID usuarioId) {
        String url = siteConfigService.obterBannerUrl(usuarioId);
        if (url == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping(value = "/blog/{postId}/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImagemPost(
            @PathVariable UUID postId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of("url", siteConfigService.uploadImagemPost(postId, file)));
    }

    @GetMapping("/blog/{postId}/imagem-url")
    public ResponseEntity<Map<String, String>> imagemPostUrl(@PathVariable UUID postId) {
        String url = siteConfigService.obterImagemPostUrl(postId);
        if (url == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(Map.of("url", url));
    }

    // ── Public (no auth required) ─────────────────────────────────────────────

    @GetMapping("/public/usuario/{usuarioId}")
    public ResponseEntity<SiteConfigDto> buscarPublico(@PathVariable UUID usuarioId) {
        SiteConfigDto dto = siteConfigService.buscarPublico(usuarioId);
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }
}

