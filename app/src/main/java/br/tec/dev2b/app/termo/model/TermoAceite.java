package br.tec.dev2b.app.termo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "termos_aceite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermoAceite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termo_id", nullable = false)
    private Termo termo;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "ip_aceite", length = 45)
    private String ipAceite;

    @Column(name = "aceito_em", nullable = false)
    private LocalDateTime aceitoEm = LocalDateTime.now();
}
