package br.tec.dev2b.app.siteconfig.dto;

import br.tec.dev2b.app.siteconfig.model.SiteConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class SiteConfigDto {

    private UUID id;
    private UUID usuarioId;
    private String linkSlug;
    private String corPrincipal;
    private String disponibilidade;
    private String logoUrl;
    private String perfilUrl;
    private String bannerUrl;
    private String sobreMim;
    private String servicos;
    private String slogan;
    private String tituloPagina;
    private String whatsapp;
    private String emailContato;
    private String telefone;
    private String websiteLink;
    private String instagram;
    private String facebook;
    private String linkedin;
    private String youtube;
    private List<BlogPostDto> posts;

    public static SiteConfigDto from(SiteConfig sc) {
        SiteConfigDto dto = new SiteConfigDto();
        dto.id = sc.getId();
        dto.usuarioId = sc.getUsuario().getId();
        dto.linkSlug = sc.getLinkSlug();
        dto.corPrincipal = sc.getCorPrincipal();
        dto.disponibilidade = sc.getDisponibilidade();
        dto.logoUrl = sc.getLogoUrl();
        dto.perfilUrl = sc.getPerfilUrl();
        dto.bannerUrl = sc.getBannerUrl();
        dto.sobreMim = sc.getSobreMim();
        dto.servicos = sc.getServicos();
        dto.slogan = sc.getSlogan();
        dto.tituloPagina = sc.getTituloPagina();
        dto.whatsapp = sc.getWhatsapp();
        dto.emailContato = sc.getEmailContato();
        dto.telefone = sc.getTelefone();
        dto.websiteLink = sc.getWebsiteLink();
        dto.instagram = sc.getInstagram();
        dto.facebook = sc.getFacebook();
        dto.linkedin = sc.getLinkedin();
        dto.youtube = sc.getYoutube();
        dto.posts = sc.getPosts().stream().map(BlogPostDto::from).collect(Collectors.toList());
        return dto;
    }
}
