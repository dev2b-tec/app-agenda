package br.tec.dev2b.app.anamnese.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "resposta_anamnese_itens",
    uniqueConstraints = @UniqueConstraint(columnNames = {"resposta_anamnese_id", "pergunta_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespostaAnamneseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resposta_anamnese_id", nullable = false)
    private RespostaAnamnese respostaAnamnese;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String opcao = "NENHUM";

    @Column(columnDefinition = "TEXT")
    private String texto;
}
