package br.tec.dev2b.app.agenda.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "controle_comissoes")
    private Boolean controleComissoes;

    @Column(name = "recurso_desativado_comissoes")
    private Boolean recursoDesativadoComissoes;

    @Column(name = "overbooking_profissionais")
    private Boolean overbookingProfissionais;

    @Column(name = "recurso_desativado_overbooking")
    private Boolean recursoDesativadoOverbooking;

    @Column(name = "fila_espera")
    private Boolean filaEspera;

    @Column(name = "recurso_desativado_fila")
    private Boolean recursoDesativadoFila;

    @Column(name = "bloquear_edicao_evolucao")
    private Boolean bloquearEdicaoEvolucao;

    @Column(name = "recurso_desativado_evolucao")
    private Boolean recursoDesativadoEvolucao;

    @Column(name = "seg_abertura")
    private LocalTime segAbertura;

    @Column(name = "seg_fechamento")
    private LocalTime segFechamento;

    @Column(name = "seg_aberto")
    private Boolean segAberto;

    @Column(name = "ter_abertura")
    private LocalTime terAbertura;

    @Column(name = "ter_fechamento")
    private LocalTime terFechamento;

    @Column(name = "ter_aberto")
    private Boolean terAberto;

    @Column(name = "qua_abertura")
    private LocalTime quaAbertura;

    @Column(name = "qua_fechamento")
    private LocalTime quaFechamento;

    @Column(name = "qua_aberto")
    private Boolean quaAberto;

    @Column(name = "qui_abertura")
    private LocalTime quiAbertura;

    @Column(name = "qui_fechamento")
    private LocalTime quiFechamento;

    @Column(name = "qui_aberto")
    private Boolean quiAberto;

    @Column(name = "sex_abertura")
    private LocalTime sexAbertura;

    @Column(name = "sex_fechamento")
    private LocalTime sexFechamento;

    @Column(name = "sex_aberto")
    private Boolean sexAberto;

    @Column(name = "sab_abertura")
    private LocalTime sabAbertura;

    @Column(name = "sab_fechamento")
    private LocalTime sabFechamento;

    @Column(name = "sab_aberto")
    private Boolean sabAberto;

    @Column(name = "dom_abertura")
    private LocalTime domAbertura;

    @Column(name = "dom_fechamento")
    private LocalTime domFechamento;

    @Column(name = "dom_aberto")
    private Boolean domAberto;

    @Column(name = "almoco_inicio")
    private LocalTime almocoInicio;

    @Column(name = "almoco_fim")
    private LocalTime almocoFim;

    @Column(name = "ativar_horario_almoco")
    private Boolean ativarHorarioAlmoco;

    @Column(name = "exibir_projecao")
    private Boolean exibirProjecao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (this.controleComissoes == null) this.controleComissoes = false;
        if (this.recursoDesativadoComissoes == null) this.recursoDesativadoComissoes = false;
        if (this.overbookingProfissionais == null) this.overbookingProfissionais = false;
        if (this.recursoDesativadoOverbooking == null) this.recursoDesativadoOverbooking = false;
        if (this.filaEspera == null) this.filaEspera = false;
        if (this.recursoDesativadoFila == null) this.recursoDesativadoFila = false;
        if (this.bloquearEdicaoEvolucao == null) this.bloquearEdicaoEvolucao = false;
        if (this.recursoDesativadoEvolucao == null) this.recursoDesativadoEvolucao = false;
        if (this.ativarHorarioAlmoco == null) this.ativarHorarioAlmoco = false;
        
        if (this.segAberto == null) this.segAberto = true;
        if (this.terAberto == null) this.terAberto = true;
        if (this.quaAberto == null) this.quaAberto = true;
        if (this.quiAberto == null) this.quiAberto = true;
        if (this.sexAberto == null) this.sexAberto = true;
        if (this.sabAberto == null) this.sabAberto = false;
        if (this.domAberto == null) this.domAberto = false;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
