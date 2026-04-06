package br.tec.dev2b.app.cnae.model;

import br.tec.dev2b.app.empresa.model.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cnae")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cnae {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "codigo_cnae", nullable = false, length = 20)
    private String codigoCnae;

    @Column(name = "tipo_tributacao", nullable = false, length = 50)
    @Builder.Default
    private String tipoTributacao = "MUNICIPIO";

    @Column(name = "discriminacao_servicos", columnDefinition = "TEXT")
    private String discriminacaoServicos;

    @Column(name = "codigo_federal", length = 20)
    private String codigoFederal;

    @Column(name = "codigo_municipal", length = 20)
    private String codigoMunicipal;

    @Column(name = "iss_retido")
    @Builder.Default
    private Boolean issRetido = false;

    @Column(name = "ir_retido")
    @Builder.Default
    private Boolean irRetido = false;

    @Column(name = "inss_retido")
    @Builder.Default
    private Boolean inssRetido = false;

    @Column(name = "csll_retido")
    @Builder.Default
    private Boolean csllRetido = false;

    @Column(name = "pis_retido")
    @Builder.Default
    private Boolean pisRetido = false;

    @Column(name = "cofins_retido")
    @Builder.Default
    private Boolean cofinsRetido = false;

    @Column(name = "aliquota_iss", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaIss = BigDecimal.ZERO;

    @Column(name = "aliquota_inss", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaInss = BigDecimal.ZERO;

    @Column(name = "aliquota_ir", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaIr = BigDecimal.ZERO;

    @Column(name = "aliquota_csll", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaCsll = BigDecimal.ZERO;

    @Column(name = "aliquota_pis", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaPis = BigDecimal.ZERO;

    @Column(name = "aliquota_cofins", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal aliquotaCofins = BigDecimal.ZERO;

    @Builder.Default
    private Boolean padrao = false;

    @Builder.Default
    private Boolean ativo = true;

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
