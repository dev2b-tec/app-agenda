package br.tec.dev2b.app.nota.model;

import br.tec.dev2b.app.paciente.model.Paciente;
import br.tec.dev2b.app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notas_compartilhadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaCompartilhada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "autor_nome", nullable = false)
    private String autorNome;

    @Column
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private String cor;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
        if (cor == null || cor.isBlank()) cor = "#FEF9C3";
    }
}
