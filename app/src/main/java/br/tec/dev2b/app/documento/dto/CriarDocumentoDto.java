package br.tec.dev2b.app.documento.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarDocumentoDto {
    private UUID empresaId;
    private UUID pacienteId;
    private UUID usuarioId;
    private String titulo;
    private String conteudo;
    private String tipo;
    private Boolean permProf;
    private Boolean permAssist;
}
