package br.tec.dev2b.app.ideia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ideia_votos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ideia_id", "empresa_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoIdeia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ideia_id", nullable = false)
    private UUID ideiaId;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
