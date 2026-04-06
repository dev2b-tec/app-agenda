package br.tec.dev2b.app.anamnese.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CriarAnamneseDto {
    private String titulo;
    private UUID empresaId;
    private List<CriarPerguntaDto> perguntas = new ArrayList<>();
}
