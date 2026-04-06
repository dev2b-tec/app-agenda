package br.tec.dev2b.app.sala.dto;

import br.tec.dev2b.app.sala.model.Sala;
import lombok.Data;

import java.util.UUID;

@Data
public class SalaDto {
    private UUID id;
    private String nome;
    private UUID unidadeId;
    private String unidadeNome;
    private Boolean ativa;
    private Boolean permitirOverbooking;
    private UUID empresaId;

    public static SalaDto from(Sala s) {
        SalaDto dto = new SalaDto();
        dto.id = s.getId();
        dto.nome = s.getNome();
        dto.unidadeId = s.getUnidade() != null ? s.getUnidade().getId() : null;
        dto.unidadeNome = s.getUnidade() != null ? s.getUnidade().getNome() : null;
        dto.ativa = s.getAtiva();
        dto.permitirOverbooking = s.getPermitirOverbooking();
        dto.empresaId = s.getEmpresa() != null ? s.getEmpresa().getId() : null;
        return dto;
    }
}
