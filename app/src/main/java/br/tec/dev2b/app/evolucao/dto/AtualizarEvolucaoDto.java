package br.tec.dev2b.app.evolucao.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AtualizarEvolucaoDto {
    private String titulo;
    private String profissional;
    private LocalDate data;
    private Boolean assinado;
    private String resumoAi;
    private String comentariosGerais;
    private String conduta;
    private String examesRealizados;
    private String prescricao;
}
