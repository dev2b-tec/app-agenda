package br.tec.dev2b.app.anamnese.dto;

import lombok.Data;

import java.util.List;

@Data
public class AtualizarAnamneseDto {
    private String titulo;
    private List<AtualizarPerguntaDto> perguntas;
}
