package br.tec.dev2b.app.anamnese.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AtualizarPerguntaDto {
    private UUID id;
    private String texto;
    private String tipoResposta;
    private Integer ordem;
    private Boolean ativa;
}
