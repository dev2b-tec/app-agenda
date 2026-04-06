package br.tec.dev2b.app.anamnese.dto;

import br.tec.dev2b.app.anamnese.model.Pergunta;
import lombok.Data;

import java.util.UUID;

@Data
public class PerguntaDto {
    private UUID id;
    private String texto;
    private String tipoResposta;
    private Integer ordem;
    private Boolean ativa;

    public static PerguntaDto from(Pergunta p) {
        PerguntaDto dto = new PerguntaDto();
        dto.id = p.getId();
        dto.texto = p.getTexto();
        dto.tipoResposta = p.getTipoResposta();
        dto.ordem = p.getOrdem();
        dto.ativa = p.getAtiva();
        return dto;
    }
}
