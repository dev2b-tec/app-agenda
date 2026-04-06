package br.tec.dev2b.app.anamnese.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SalvarRespostaAnamneseDto {

    private UUID pacienteId;
    private UUID anamneseId;
    private String profissional;
    private List<ItemDto> itens;

    @Data
    public static class ItemDto {
        private UUID perguntaId;
        private String opcao;   // SIM | NAO | NENHUM
        private String texto;
    }
}
