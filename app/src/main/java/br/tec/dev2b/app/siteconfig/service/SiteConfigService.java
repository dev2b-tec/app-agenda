package br.tec.dev2b.app.siteconfig.service;

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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteConfigService {

    private final SiteConfigRepository siteConfigRepository;
    private final BlogPostRepository blogPostRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public SiteConfigDto buscarPorUsuario(UUID usuarioId) {
        return siteConfigRepository.findByUsuarioId(usuarioId)
                .map(SiteConfigDto::from)
                .orElse(null);
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
}
