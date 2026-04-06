package br.tec.dev2b.app.cnae.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CriarCnaeDto {
    private UUID empresaId;
    private String codigoCnae;
    private String tipoTributacao;
    private String discriminacaoServicos;
    private String codigoFederal;
    private String codigoMunicipal;
    private Boolean issRetido;
    private Boolean irRetido;
    private Boolean inssRetido;
    private Boolean csllRetido;
    private Boolean pisRetido;
    private Boolean cofinsRetido;
    private BigDecimal aliquotaIss;
    private BigDecimal aliquotaInss;
    private BigDecimal aliquotaIr;
    private BigDecimal aliquotaCsll;
    private BigDecimal aliquotaPis;
    private BigDecimal aliquotaCofins;
    private Boolean padrao;
    private Boolean ativo;
}
