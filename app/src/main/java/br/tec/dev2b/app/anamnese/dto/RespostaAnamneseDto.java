package br.tec.dev2b.app.anamnese.dto;

import br.tec.dev2b.app.anamnese.model.RespostaAnamnese;
import br.tec.dev2b.app.anamnese.model.RespostaAnamneseItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RespostaAnamneseDto {

    private UUID id;
    private UUID pacienteId;
    private UUID anamneseId;
    private String profissional;
    private LocalDate data;
    private List<ItemDto> itens;

    @Getter
    @Builder
    public static class ItemDto {
        private UUID id;
        private UUID perguntaId;
        private String perguntaTexto;
        private String opcao;
        private String texto;

        public static ItemDto from(RespostaAnamneseItem item) {
            return ItemDto.builder()
                    .id(item.getId())
                    .perguntaId(item.getPergunta().getId())
                    .perguntaTexto(item.getPergunta().getTexto())
                    .opcao(item.getOpcao())
                    .texto(item.getTexto())
                    .build();
        }
    }

    public static RespostaAnamneseDto from(RespostaAnamnese r) {
        return RespostaAnamneseDto.builder()
                .id(r.getId())
                .pacienteId(r.getPaciente().getId())
                .anamneseId(r.getAnamnese().getId())
                .profissional(r.getProfissional())
                .data(r.getData())
                .itens(r.getItens().stream().map(ItemDto::from).toList())
                .build();
    }
}
