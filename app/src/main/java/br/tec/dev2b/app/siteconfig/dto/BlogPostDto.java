package br.tec.dev2b.app.siteconfig.dto;

import br.tec.dev2b.app.siteconfig.model.BlogPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BlogPostDto {

    private UUID id;
    private String titulo;
    private LocalDate dataPublicacao;
    private String status;
    private String conteudo;
    private String imagemUrl;

    public static BlogPostDto from(BlogPost p) {
        BlogPostDto dto = new BlogPostDto();
        dto.id = p.getId();
        dto.titulo = p.getTitulo();
        dto.dataPublicacao = p.getDataPublicacao();
        dto.status = p.getStatus();
        dto.conteudo = p.getConteudo();
        dto.imagemUrl = p.getImagemUrl();
        return dto;
    }
}
