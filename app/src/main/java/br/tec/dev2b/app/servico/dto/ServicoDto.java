package br.tec.dev2b.app.servico.dto;

import br.tec.dev2b.app.servico.model.Servico;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ServicoDto {
    private UUID id;
    private String nome;
    private String tipo;
    private String categoria;
    private String descricao;
    private String tipoComissao;
    private Integer duracaoMinutos;
    private BigDecimal valor;
    private BigDecimal valorCusto;
    private BigDecimal valorNaoComissionavel;
    private Boolean ativo;
    private UUID empresaId;

    public static ServicoDto from(Servico s) {
        ServicoDto dto = new ServicoDto();
        dto.id = s.getId();
        dto.nome = s.getNome();
        dto.tipo = s.getTipo();
        dto.categoria = s.getCategoria();
        dto.descricao = s.getDescricao();
        dto.tipoComissao = s.getTipoComissao();
        dto.duracaoMinutos = s.getDuracaoMinutos();
        dto.valor = s.getValor();
        dto.valorCusto = s.getValorCusto();
        dto.valorNaoComissionavel = s.getValorNaoComissionavel();
        dto.ativo = s.getAtivo();
        dto.empresaId = s.getEmpresa() != null ? s.getEmpresa().getId() : null;
        return dto;
    }
}
