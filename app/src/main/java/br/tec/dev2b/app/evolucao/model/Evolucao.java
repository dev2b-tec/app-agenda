package br.tec.dev2b.app.evolucao.model;

import br.tec.dev2b.app.paciente.model.Paciente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evolucoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private String titulo;

    @Column(nullable = false)
    private String profissional;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Boolean assinado;

    @Column(name = "resumo_ai", columnDefinition = "TEXT")
    private String resumoAi;

    @Column(name = "comentarios_gerais", columnDefinition = "TEXT")
    private String comentariosGerais;

    @Column(columnDefinition = "TEXT")
    private String conduta;

    @Column(name = "exames_realizados", columnDefinition = "TEXT")
    private String examesRealizados;

    @Column(columnDefinition = "TEXT")
    private String prescricao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.assinado == null) this.assinado = false;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
