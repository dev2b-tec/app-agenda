package br.tec.dev2b.app.agenda.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class AtualizarAgendaDto {
    private Boolean controleComissoes;
    private Boolean recursoDesativadoComissoes;
    private Boolean overbookingProfissionais;
    private Boolean recursoDesativadoOverbooking;
    private Boolean filaEspera;
    private Boolean recursoDesativadoFila;
    private Boolean bloquearEdicaoEvolucao;
    private Boolean recursoDesativadoEvolucao;
    
    private LocalTime segAbertura;
    private LocalTime segFechamento;
    private Boolean segAberto;
    
    private LocalTime terAbertura;
    private LocalTime terFechamento;
    private Boolean terAberto;
    
    private LocalTime quaAbertura;
    private LocalTime quaFechamento;
    private Boolean quaAberto;
    
    private LocalTime quiAbertura;
    private LocalTime quiFechamento;
    private Boolean quiAberto;
    
    private LocalTime sexAbertura;
    private LocalTime sexFechamento;
    private Boolean sexAberto;
    
    private LocalTime sabAbertura;
    private LocalTime sabFechamento;
    private Boolean sabAberto;
    
    private LocalTime domAbertura;
    private LocalTime domFechamento;
    private Boolean domAberto;
    
    private LocalTime almocoInicio;
    private LocalTime almocoFim;
    private Boolean ativarHorarioAlmoco;
    private Boolean exibirProjecao;
}
