package br.tec.dev2b.app.marcador.dto;

import java.util.UUID;

public record CriarMarcadorDto(
        String tipo,
        String cor,
        UUID empresaId
) {}
