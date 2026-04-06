package br.tec.dev2b.app.senha.dto;

import br.tec.dev2b.app.senha.model.Senha;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SenhaDto {
    private UUID id;
    private String tipo;
    private String descricao;
    private String valor;
    private UUID usuarioId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SenhaDto from(Senha s) {
        SenhaDto dto = new SenhaDto();
        dto.id = s.getId();
        dto.tipo = s.getTipo();
        dto.descricao = s.getDescricao();
        dto.valor = s.getValor();
        dto.usuarioId = s.getUsuario() != null ? s.getUsuario().getId() : null;
        dto.createdAt = s.getCreatedAt();
        dto.updatedAt = s.getUpdatedAt();
        return dto;
    }
}
