package br.tec.dev2b.app.ideia.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComentarioDto(
        UUID id,
        UUID ideiaId,
        UUID empresaId,
        String nomeEmpresa,
        String texto,
        LocalDateTime createdAt
) {}
