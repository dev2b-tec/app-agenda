package br.tec.dev2b.app.mensagem.dto;

import br.tec.dev2b.app.mensagem.model.MensagemPadrao;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MensagemPadraoDto {

    private UUID id;
    private UUID empresaId;
    private TipoMensagemPadrao tipo;
    private String texto;
    private Boolean isDefault;

    public static MensagemPadraoDto from(MensagemPadrao m) {
        return MensagemPadraoDto.builder()
                .id(m.getId())
                .empresaId(m.getEmpresaId())
                .tipo(m.getTipo())
                .texto(m.getTexto())
                .isDefault(m.getIsDefault())
                .build();
    }
}
