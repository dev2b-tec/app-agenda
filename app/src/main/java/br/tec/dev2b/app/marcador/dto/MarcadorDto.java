package br.tec.dev2b.app.marcador.dto;

import br.tec.dev2b.app.marcador.model.Marcador;

import java.util.UUID;

public record MarcadorDto(
        UUID id,
        String tipo,
        String cor,
        UUID empresaId
) {
    public static MarcadorDto from(Marcador m) {
        return new MarcadorDto(
                m.getId(),
                m.getTipo(),
                m.getCor(),
                m.getEmpresa().getId()
        );
    }
}
