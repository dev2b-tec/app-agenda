package br.tec.dev2b.app.integracao.dto;

import br.tec.dev2b.app.integracao.model.Integracao;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class IntegracaoDto {
    private UUID id;
    private UUID empresaId;
    private String tipo;
    private Boolean ativo;
    private Map<String, String> configuracao;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static IntegracaoDto from(Integracao i) {
        IntegracaoDto dto = new IntegracaoDto();
        dto.id = i.getId();
        dto.empresaId = i.getEmpresa() != null ? i.getEmpresa().getId() : null;
        dto.tipo = i.getTipo();
        dto.ativo = i.getAtivo();
        dto.configuracao = i.getConfiguracao();
        dto.criadoEm = i.getCriadoEm();
        dto.atualizadoEm = i.getAtualizadoEm();
        return dto;
    }
}
