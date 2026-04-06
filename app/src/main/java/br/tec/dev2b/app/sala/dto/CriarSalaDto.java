package br.tec.dev2b.app.sala.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarSalaDto {
    private String nome;
    private UUID unidadeId;
    private Boolean permitirOverbooking;
    private UUID empresaId;
}
