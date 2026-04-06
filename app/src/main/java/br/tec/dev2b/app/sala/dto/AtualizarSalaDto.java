package br.tec.dev2b.app.sala.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AtualizarSalaDto {
    private String nome;
    private UUID unidadeId;
    private Boolean ativa;
    private Boolean permitirOverbooking;
}
