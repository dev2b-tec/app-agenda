package br.tec.dev2b.app.termo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermoDto {
    private UUID id;
    private String titulo;
    private String conteudo;
    private String versao;
    private String tipo;
    private Boolean ativo;
    private LocalDateTime createdAt;
    private Boolean aceito;
}
