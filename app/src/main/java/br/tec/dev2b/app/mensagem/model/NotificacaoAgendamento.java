package br.tec.dev2b.app.mensagem.model;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registra cada notificação WhatsApp automática enviada para um agendamento.
 * A constraint UNIQUE (agendamento_id, tipo) impede reenvio do mesmo tipo.
 */
@Entity
@Table(name = "notificacoes_agendamento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"agendamento_id", "tipo"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoMensagemPadrao tipo;

    @Column(name = "enviado_em", nullable = false)
    @Builder.Default
    private LocalDateTime enviadoEm = LocalDateTime.now();

    @Column(name = "numero_destino", length = 30)
    private String numeroDestino;
}
