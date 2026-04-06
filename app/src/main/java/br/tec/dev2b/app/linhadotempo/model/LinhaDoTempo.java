package br.tec.dev2b.app.linhadotempo.model;

import br.tec.dev2b.app.paciente.model.Paciente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "linha_do_tempo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinhaDoTempo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false, length = 50)
    private String tipo;

    private String titulo;

    private String profissional;

    @Column(name = "referencia_id")
    private UUID referenciaId;

    @Column(nullable = false)
    private LocalDate data;

    private LocalTime hora;

    @Column(nullable = false)
    private Boolean assinado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.assinado == null) this.assinado = false;
    }
}
