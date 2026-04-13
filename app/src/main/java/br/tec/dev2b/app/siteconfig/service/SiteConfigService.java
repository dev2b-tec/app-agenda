package br.tec.dev2b.app.siteconfig.service;

import br.tec.dev2b.app.infra.minio.MinioService;
import br.tec.dev2b.app.siteconfig.dto.*;
import br.tec.dev2b.app.siteconfig.model.BlogPost;
import br.tec.dev2b.app.siteconfig.model.SiteConfig;
import br.tec.dev2b.app.siteconfig.repository.BlogPostRepository;
import br.tec.dev2b.app.siteconfig.repository.SiteConfigRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigRepository siteConfigRepository;
    private final BlogPostRepository blogPostRepository;
    private final UsuarioRepository usuarioRepository;
    private final MinioService minioService;

    private String obterExtensao(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf('.'));
    }

    @Transactional(readOnly = true)
    public SiteConfigDto buscarPorUsuario(UUID usuarioId) {
        return siteConfigRepository.findByUsuarioId(usuarioId)
                .map(SiteConfigDto::from)
                .orElse(null);
    }

    /** Public endpoint — resolves presigned image URLs in the response. */
    @Transactional(readOnly = true)
    public SiteConfigDto buscarPublico(UUID usuarioId) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId).orElse(null);
        if (sc == null || !"ativo".equalsIgnoreCase(sc.getDisponibilidade())) return null;
        SiteConfigDto dto = SiteConfigDto.from(sc);
        String bucket = minioService.getBucketFotos();
        if (sc.getLogoUrl()    != null) dto.setLogoUrl(minioService.getPublicUrl(bucket, sc.getLogoUrl()));
        if (sc.getPerfilUrl()  != null) dto.setPerfilUrl(minioService.getPublicUrl(bucket, sc.getPerfilUrl()));
        if (sc.getBannerUrl()  != null) dto.setBannerUrl(minioService.getPublicUrl(bucket, sc.getBannerUrl()));
        if (dto.getPosts() != null) {
            dto.getPosts().forEach(p -> {
                BlogPost post = blogPostRepository.findById(p.getId()).orElse(null);
                if (post != null && post.getImagemUrl() != null) {
                    p.setImagemUrl(minioService.getPublicUrl(bucket, post.getImagemUrl()));
                }
            });
        }
        return dto;
    }


    @Transactional
    public SiteConfigDto salvar(UUID usuarioId, SalvarSiteConfigDto dto) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
            SiteConfig novo = new SiteConfig();
            novo.setUsuario(usuario);
            novo.setDisponibilidade("ativo");
            return novo;
        });

        if (dto.getLinkSlug()     != null) sc.setLinkSlug(dto.getLinkSlug());
        if (dto.getCorPrincipal() != null) sc.setCorPrincipal(dto.getCorPrincipal());
        if (dto.getDisponibilidade() != null) sc.setDisponibilidade(dto.getDisponibilidade());
        if (dto.getSobreMim()     != null) sc.setSobreMim(dto.getSobreMim());
        if (dto.getServicos()     != null) sc.setServicos(dto.getServicos());
        if (dto.getSlogan()       != null) sc.setSlogan(dto.getSlogan());
        if (dto.getTituloPagina() != null) sc.setTituloPagina(dto.getTituloPagina());
        if (dto.getWhatsapp()     != null) sc.setWhatsapp(dto.getWhatsapp());
        if (dto.getEmailContato() != null) sc.setEmailContato(dto.getEmailContato());
        if (dto.getTelefone()     != null) sc.setTelefone(dto.getTelefone());
        if (dto.getWebsiteLink()  != null) sc.setWebsiteLink(dto.getWebsiteLink());
        if (dto.getInstagram()    != null) sc.setInstagram(dto.getInstagram());
        if (dto.getFacebook()     != null) sc.setFacebook(dto.getFacebook());
        if (dto.getLinkedin()     != null) sc.setLinkedin(dto.getLinkedin());
        if (dto.getYoutube()      != null) sc.setYoutube(dto.getYoutube());

        return SiteConfigDto.from(siteConfigRepository.save(sc));
    }

    @Transactional
    public BlogPostDto criarPost(UUID usuarioId, CriarBlogPostDto dto) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Configure o site antes de adicionar posts."));

        BlogPost post = new BlogPost();
        post.setSiteConfig(sc);
        post.setTitulo(dto.getTitulo());
        post.setDataPublicacao(dto.getDataPublicacao());
        post.setStatus(dto.getStatus() != null ? dto.getStatus() : "RASCUNHO");
        post.setConteudo(dto.getConteudo());

        return BlogPostDto.from(blogPostRepository.save(post));
    }

    @Transactional
    public void deletarPost(UUID postId) {
        blogPostRepository.deleteById(postId);
    }

    @Transactional
    public BlogPostDto atualizarPost(UUID postId, AtualizarBlogPostDto dto) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não encontrado: " + postId));
        if (dto.getTitulo()          != null) post.setTitulo(dto.getTitulo());
        if (dto.getDataPublicacao()  != null) post.setDataPublicacao(dto.getDataPublicacao());
        if (dto.getStatus()          != null) post.setStatus(dto.getStatus());
        if (dto.getConteudo()        != null) post.setConteudo(dto.getConteudo());
        return BlogPostDto.from(blogPostRepository.save(post));
    }

    // ── Image uploads ─────────────────────────────────────────────────────────

    @Transactional
    public String uploadLogo(UUID usuarioId, MultipartFile file) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Configure o site antes de enviar imagens."));
        String objectName = "site-config/" + usuarioId + "/logo" + obterExtensao(file.getOriginalFilename());
        String bucket = minioService.getBucketFotos();
        minioService.upload(bucket, objectName, file);
        sc.setLogoUrl(objectName);
        siteConfigRepository.save(sc);
        return minioService.getPublicUrl(bucket, objectName);
    }

    @Transactional(readOnly = true)
    public String obterLogoUrl(UUID usuarioId) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("SiteConfig não encontrado"));
        if (sc.getLogoUrl() == null) return null;
        return minioService.getPublicUrl(minioService.getBucketFotos(), sc.getLogoUrl());
    }

    @Transactional
    public String uploadPerfil(UUID usuarioId, MultipartFile file) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Configure o site antes de enviar imagens."));
        String objectName = "site-config/" + usuarioId + "/perfil" + obterExtensao(file.getOriginalFilename());
        String bucket = minioService.getBucketFotos();
        minioService.upload(bucket, objectName, file);
        sc.setPerfilUrl(objectName);
        siteConfigRepository.save(sc);
        return minioService.getPublicUrl(bucket, objectName);
    }

    @Transactional(readOnly = true)
    public String obterPerfilUrl(UUID usuarioId) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("SiteConfig não encontrado"));
        if (sc.getPerfilUrl() == null) return null;
        return minioService.getPublicUrl(minioService.getBucketFotos(), sc.getPerfilUrl());
    }

    @Transactional
    public String uploadBanner(UUID usuarioId, MultipartFile file) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Configure o site antes de enviar imagens."));
        String objectName = "site-config/" + usuarioId + "/banner" + obterExtensao(file.getOriginalFilename());
        String bucket = minioService.getBucketFotos();
        minioService.upload(bucket, objectName, file);
        sc.setBannerUrl(objectName);
        siteConfigRepository.save(sc);
        return minioService.getPublicUrl(bucket, objectName);
    }

    @Transactional(readOnly = true)
    public String obterBannerUrl(UUID usuarioId) {
        SiteConfig sc = siteConfigRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("SiteConfig não encontrado"));
        if (sc.getBannerUrl() == null) return null;
        return minioService.getPublicUrl(minioService.getBucketFotos(), sc.getBannerUrl());
    }

    @Transactional
    public String uploadImagemPost(UUID postId, MultipartFile file) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não encontrado: " + postId));
        String objectName = "site-config/posts/" + postId + "/imagem" + obterExtensao(file.getOriginalFilename());
        String bucket = minioService.getBucketFotos();
        minioService.upload(bucket, objectName, file);
        post.setImagemUrl(objectName);
        blogPostRepository.save(post);
        return minioService.getPublicUrl(bucket, objectName);
    }

    @Transactional(readOnly = true)
    public String obterImagemPostUrl(UUID postId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post não encontrado: " + postId));
        if (post.getImagemUrl() == null) return null;
        return minioService.getPublicUrl(minioService.getBucketFotos(), post.getImagemUrl());
    }
}

