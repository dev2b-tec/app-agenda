package br.tec.dev2b.app.paciente.model;

import br.tec.dev2b.app.empresa.model.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String genero;

    @Column(length = 100)
    private String plano;

    @Column(name = "numero_carteirinha", length = 50)
    private String numeroCarteirinha;

    @Column(length = 100)
    private String grupo;

    @Column(name = "como_conheceu", length = 100)
    private String comoConheceu;

    @Column(length = 20)
    private String rg;

    @Column(length = 14)
    private String cpf;

    @Column(length = 10)
    private String cep;

    private String email;

    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(name = "outras_informacoes", columnDefinition = "TEXT")
    private String outrasInformacoes;

    @Column(name = "nome_responsavel")
    private String nomeResponsavel;

    @Column(name = "data_nascimento_resp")
    private LocalDate dataNascimentoResp;

    @Column(name = "cpf_responsavel", length = 14)
    private String cpfResponsavel;

    @Column(name = "telefone_responsavel", length = 20)
    private String telefoneResponsavel;

    @Column(name = "status_pagamento", length = 50)
    @Builder.Default
    private String statusPagamento = "EM_ABERTO";

    @Builder.Default
    private Integer sessoes = 0;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

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
