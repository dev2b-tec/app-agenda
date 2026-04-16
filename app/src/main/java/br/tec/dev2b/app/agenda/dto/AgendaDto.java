package br.tec.dev2b.app.agenda.dto;

import br.tec.dev2b.app.agenda.model.Agenda;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class AgendaDto {
    private UUID id;
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

    public static AgendaDto from(Agenda a) {
        AgendaDto dto = new AgendaDto();
        dto.id = a.getId();
        dto.controleComissoes = a.getControleComissoes();
        dto.recursoDesativadoComissoes = a.getRecursoDesativadoComissoes();
        dto.overbookingProfissionais = a.getOverbookingProfissionais();
        dto.recursoDesativadoOverbooking = a.getRecursoDesativadoOverbooking();
        dto.filaEspera = a.getFilaEspera();
        dto.recursoDesativadoFila = a.getRecursoDesativadoFila();
        dto.bloquearEdicaoEvolucao = a.getBloquearEdicaoEvolucao();
        dto.recursoDesativadoEvolucao = a.getRecursoDesativadoEvolucao();
        
        dto.segAbertura = a.getSegAbertura();
        dto.segFechamento = a.getSegFechamento();
        dto.segAberto = a.getSegAberto();
        
        dto.terAbertura = a.getTerAbertura();
        dto.terFechamento = a.getTerFechamento();
        dto.terAberto = a.getTerAberto();
        
        dto.quaAbertura = a.getQuaAbertura();
        dto.quaFechamento = a.getQuaFechamento();
        dto.quaAberto = a.getQuaAberto();
        
        dto.quiAbertura = a.getQuiAbertura();
        dto.quiFechamento = a.getQuiFechamento();
        dto.quiAberto = a.getQuiAberto();
        
        dto.sexAbertura = a.getSexAbertura();
        dto.sexFechamento = a.getSexFechamento();
        dto.sexAberto = a.getSexAberto();
        
        dto.sabAbertura = a.getSabAbertura();
        dto.sabFechamento = a.getSabFechamento();
        dto.sabAberto = a.getSabAberto();
        
        dto.domAbertura = a.getDomAbertura();
        dto.domFechamento = a.getDomFechamento();
        dto.domAberto = a.getDomAberto();
        
        dto.almocoInicio = a.getAlmocoInicio();
        dto.almocoFim = a.getAlmocoFim();
        dto.ativarHorarioAlmoco = a.getAtivarHorarioAlmoco();
        dto.exibirProjecao = a.getExibirProjecao();
        
        return dto;
    }
}
