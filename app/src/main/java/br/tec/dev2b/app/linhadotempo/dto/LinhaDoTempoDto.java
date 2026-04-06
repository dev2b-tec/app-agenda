package br.tec.dev2b.app.linhadotempo.dto;

import br.tec.dev2b.app.linhadotempo.model.LinhaDoTempo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class LinhaDoTempoDto {

    private UUID id;
    private UUID pacienteId;
    private String tipo;
    private String titulo;
    private String profissional;
    private UUID referenciaId;
    private LocalDate data;
    private LocalTime hora;
    private Boolean assinado;

    public static LinhaDoTempoDto from(LinhaDoTempo e) {
        return LinhaDoTempoDto.builder()
                .id(e.getId())
                .pacienteId(e.getPaciente().getId())
                .tipo(e.getTipo())
                .titulo(e.getTitulo())
                .profissional(e.getProfissional())
                .referenciaId(e.getReferenciaId())
                .data(e.getData())
                .hora(e.getHora())
                .assinado(e.getAssinado())
                .build();
    }
}
