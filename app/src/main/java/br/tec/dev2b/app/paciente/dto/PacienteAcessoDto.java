package br.tec.dev2b.app.paciente.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PacienteAcessoDto {
    private UUID usuarioId;
    private String usuarioNome;
}
