package br.tec.dev2b.app.anamnese.dto;

import lombok.Data;

@Data
public class CriarPerguntaDto {
    private String texto;
    private String tipoResposta = "AMBOS";
    private Integer ordem = 0;
}
