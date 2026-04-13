package br.tec.dev2b.app.ideia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ideias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ideia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, insertable = false, updatable = false)
    private Integer numero;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String situacao = "MODERACAO";

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "nome_empresa")
    private String nomeEmpresa;

    @Column(name = "ideia_legal", nullable = false)
    @Builder.Default
    private Boolean ideiaLegal = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ideia_categorias", joinColumns = @JoinColumn(name = "ideia_id"))
    @Column(name = "categoria")
    @Builder.Default
    private List<String> categorias = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ideia_imagens", joinColumns = @JoinColumn(name = "ideia_id"))
    @Column(name = "url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
