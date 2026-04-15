package br.tec.dev2b.app.nota.dto;

import br.tec.dev2b.app.nota.model.NotaCompartilhada;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotaCompartilhadaDto {
    private UUID id;
    private UUID pacienteId;
    private UUID autorId;
    private String autorNome;
    private String titulo;
    private String texto;
    private String cor;
    private LocalDateTime criadoEm;

    public static NotaCompartilhadaDto from(NotaCompartilhada n) {
        NotaCompartilhadaDto dto = new NotaCompartilhadaDto();
        dto.setId(n.getId());
        dto.setPacienteId(n.getPaciente().getId());
        dto.setAutorId(n.getAutor().getId());
        dto.setAutorNome(n.getAutorNome());
        dto.setTitulo(n.getTitulo());
        dto.setTexto(n.getTexto());
        dto.setCor(n.getCor());
        dto.setCriadoEm(n.getCriadoEm());
        return dto;
    }
}
