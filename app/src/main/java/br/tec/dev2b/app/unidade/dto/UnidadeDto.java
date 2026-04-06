package br.tec.dev2b.app.unidade.dto;

import br.tec.dev2b.app.unidade.model.Unidade;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UnidadeDto {
    private UUID id;
    private String nome;
    private String descricao;
    private Boolean ativa;
    private UUID empresaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UnidadeDto from(Unidade u) {
        UnidadeDto dto = new UnidadeDto();
        dto.id = u.getId();
        dto.nome = u.getNome();
        dto.descricao = u.getDescricao();
        dto.ativa = u.getAtiva();
        dto.empresaId = u.getEmpresa() != null ? u.getEmpresa().getId() : null;
        dto.createdAt = u.getCreatedAt();
        dto.updatedAt = u.getUpdatedAt();
        return dto;
    }
}
