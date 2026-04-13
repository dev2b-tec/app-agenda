package br.tec.dev2b.app.ideia.dto;

import java.util.List;
import java.util.UUID;

public record CriarIdeiaDto(
        String titulo,
        String descricao,
        UUID empresaId,
        List<String> categorias
) {}
