package br.tec.dev2b.app.siteconfig.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AtualizarBlogPostDto {
    private String titulo;
    private LocalDate dataPublicacao;
    private String status;
    private String conteudo;
}
