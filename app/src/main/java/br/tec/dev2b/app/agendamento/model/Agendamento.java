package br.tec.dev2b.app.agendamento.model;

import br.tec.dev2b.app.empresa.model.Empresa;
import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /** Denormalized — kept in sync on create/update */
    @Column(name = "paciente_nome")
    private String pacienteNome;

    /** Denormalized */
    @Column(name = "usuario_nome")
    private String usuarioNome;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    /**
     * Aguardando | Confirmado | Atendido | Faltou | Cancelado
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "Aguardando";

    @Column
    private String sala;

    @Column
    @Builder.Default
    private Boolean recorrente = false;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    /** UI color hint: green | red | teal | purple */
    @Column
    @Builder.Default
    private String cor = "purple";

    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AgendamentoServico> servicos = new ArrayList<>();

    @Column(name = "valor_total", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "valor_recebido", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal valorRecebido = BigDecimal.ZERO;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "metodo_pagamento", length = 50)
    private String metodoPagamento;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
