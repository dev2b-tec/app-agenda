package br.tec.dev2b.app.ideia.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record IdeiaDto(
        UUID id,
        Integer numero,
        String titulo,
        String descricao,
        String situacao,
        UUID empresaId,
        String nomeEmpresa,
        boolean ideiaLegal,
        long totalVotos,
        long totalComentarios,
        boolean jaVotou,
        List<String> categorias,
        List<String> imageUrls,
        LocalDateTime createdAt
) {}
