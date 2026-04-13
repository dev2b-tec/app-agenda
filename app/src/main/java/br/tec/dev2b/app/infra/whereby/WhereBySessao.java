package br.tec.dev2b.app.infra.whereby;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "whereby_sessoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhereBySessao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** FK para o agendamento — nullable via ON DELETE SET NULL */
    @Column(name = "agendamento_id")
    private UUID agendamentoId;

    @Column(name = "meeting_id", nullable = false, length = 255)
    private String meetingId;

    @Column(name = "participant_id", length = 255)
    private String participantId;

    @Column(name = "participant_name", length = 255)
    private String participantName;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /** null enquanto o participante está na sala */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * Duração em minutos arredondada para cima:
     *  1s  → 1 min | 60s → 1 min | 61s → 2 min
     * Preenchido quando ended_at é registrado.
     */
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
