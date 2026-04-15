package br.tec.dev2b.app.nota.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarNotaDto {
    private UUID pacienteId;
    private UUID autorId;
    private String autorNome;
    private String titulo;
    private String texto;
    private String cor;
}
