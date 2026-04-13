package br.tec.dev2b.app.ideia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ideia_comentarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComentarioIdeia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ideia_id", nullable = false)
    private UUID ideiaId;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "nome_empresa")
    private String nomeEmpresa;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
