package br.tec.dev2b.app.usuario.model;

import br.tec.dev2b.app.empresa.model.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "assinatura_url")
    private String assinaturaUrl;

    @Column
    private String telefone;

    @Column
    private String cep;

    @Column
    private String logradouro;

    @Column
    private String numero;

    @Column
    private String complemento;

    @Column
    private String bairro;

    @Column
    private String cidade;

    @Column
    private String tipo;

    @Column
    private String conselho;

    @Column(name = "numero_conselho")
    private String numeroConselho;

    @Column
    private String especialidade;

    @Column
    private String genero;

    @Column(name = "duracao_sessao")
    private Integer duracaoSessao;

    @Column(name = "periodo_minimo")
    private String periodoMinimo;

    @Column(name = "periodo_maximo")
    private String periodoMaximo;

    @Column(name = "tempo_antecedencia")
    private String tempoAntecedencia;

    @Column(nullable = false)
    private Boolean disponivel = true;

    @Column(name = "telefone_comercial")
    private String telefoneComercial;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id")
    private br.tec.dev2b.app.agenda.model.Agenda agenda;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
