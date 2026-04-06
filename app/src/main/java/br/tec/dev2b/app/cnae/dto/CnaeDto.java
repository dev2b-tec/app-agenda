package br.tec.dev2b.app.cnae.dto;

import br.tec.dev2b.app.cnae.model.Cnae;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CnaeDto {
    private UUID id;
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

    public static CnaeDto from(Cnae cnae) {
        CnaeDto dto = new CnaeDto();
        dto.id = cnae.getId();
        dto.empresaId = cnae.getEmpresa() != null ? cnae.getEmpresa().getId() : null;
        dto.codigoCnae = cnae.getCodigoCnae();
        dto.tipoTributacao = cnae.getTipoTributacao();
        dto.discriminacaoServicos = cnae.getDiscriminacaoServicos();
        dto.codigoFederal = cnae.getCodigoFederal();
        dto.codigoMunicipal = cnae.getCodigoMunicipal();
        dto.issRetido = cnae.getIssRetido();
        dto.irRetido = cnae.getIrRetido();
        dto.inssRetido = cnae.getInssRetido();
        dto.csllRetido = cnae.getCsllRetido();
        dto.pisRetido = cnae.getPisRetido();
        dto.cofinsRetido = cnae.getCofinsRetido();
        dto.aliquotaIss = cnae.getAliquotaIss();
        dto.aliquotaInss = cnae.getAliquotaInss();
        dto.aliquotaIr = cnae.getAliquotaIr();
        dto.aliquotaCsll = cnae.getAliquotaCsll();
        dto.aliquotaPis = cnae.getAliquotaPis();
        dto.aliquotaCofins = cnae.getAliquotaCofins();
        dto.padrao = cnae.getPadrao();
        dto.ativo = cnae.getAtivo();
        return dto;
    }
}
