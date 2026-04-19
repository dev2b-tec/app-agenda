package br.tec.dev2b.app.paciente.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConcederAcessoDto {
    private UUID usuarioId;
}
