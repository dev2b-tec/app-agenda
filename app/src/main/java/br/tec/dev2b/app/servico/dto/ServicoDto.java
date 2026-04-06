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
    private BigDecimal valor;
    private Boolean ativo;
    private UUID empresaId;

    public static ServicoDto from(Servico s) {
        ServicoDto dto = new ServicoDto();
        dto.id = s.getId();
        dto.nome = s.getNome();
        dto.tipo = s.getTipo();
        dto.valor = s.getValor();
        dto.ativo = s.getAtivo();
        dto.empresaId = s.getEmpresa() != null ? s.getEmpresa().getId() : null;
        return dto;
    }
}
