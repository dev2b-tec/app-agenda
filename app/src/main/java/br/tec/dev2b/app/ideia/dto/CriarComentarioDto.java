package br.tec.dev2b.app.ideia.dto;

import java.util.UUID;

public record CriarComentarioDto(
        String texto,
        UUID empresaId
) {}
