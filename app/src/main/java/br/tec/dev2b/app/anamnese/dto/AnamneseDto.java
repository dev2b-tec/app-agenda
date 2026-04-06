package br.tec.dev2b.app.anamnese.dto;

import br.tec.dev2b.app.anamnese.model.Anamnese;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AnamneseDto {
    private UUID id;
    private String titulo;
    private UUID empresaId;
    private List<PerguntaDto> perguntas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AnamneseDto from(Anamnese a) {
        AnamneseDto dto = new AnamneseDto();
        dto.id = a.getId();
        dto.titulo = a.getTitulo();
        dto.empresaId = a.getEmpresa() != null ? a.getEmpresa().getId() : null;
        dto.perguntas = a.getPerguntas().stream().map(PerguntaDto::from).toList();
        dto.createdAt = a.getCreatedAt();
        dto.updatedAt = a.getUpdatedAt();
        return dto;
    }
}
