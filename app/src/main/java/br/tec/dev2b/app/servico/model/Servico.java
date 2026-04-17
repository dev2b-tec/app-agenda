package br.tec.dev2b.app.servico.model;

import br.tec.dev2b.app.empresa.model.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "servicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String tipo = "GERAL";

    @Column(length = 255)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "tipo_comissao", length = 50)
    @Builder.Default
    private String tipoComissao = "NAO_GERAR";

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_custo", precision = 10, scale = 2)
    private BigDecimal valorCusto;

    @Column(name = "valor_nao_comissionavel", precision = 10, scale = 2)
    private BigDecimal valorNaoComissionavel;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

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
