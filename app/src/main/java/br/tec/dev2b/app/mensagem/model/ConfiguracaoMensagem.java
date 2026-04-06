package br.tec.dev2b.app.mensagem.model;

import br.tec.dev2b.app.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "configuracoes_mensagens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracaoMensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "numero_whatsapp", length = 20)
    private String numeroWhatsapp;

    @Column(name = "permitir_profissionais", nullable = false)
    @Builder.Default
    private Boolean permitirProfissionais = false;

    @Column(name = "envio_sms_automatico", nullable = false)
    @Builder.Default
    private Boolean envioSmsAutomatico = false;

    @Column(name = "enviar_com_risco_falta", nullable = false)
    @Builder.Default
    private Boolean enviarComRiscoFalta = false;

    @Column(name = "horario_disparo", length = 20)
    private String horarioDisparo;

    @Column(name = "mensagem_lembrete", columnDefinition = "TEXT")
    private String mensagemLembrete;

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
