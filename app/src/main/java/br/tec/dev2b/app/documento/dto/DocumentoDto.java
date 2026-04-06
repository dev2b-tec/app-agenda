package br.tec.dev2b.app.documento.dto;

import br.tec.dev2b.app.documento.model.Documento;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DocumentoDto {

    private UUID id;
    private UUID empresaId;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID usuarioId;
    private String usuarioNome;
    private String titulo;
    private String conteudo;
    private String tipo;
    private Boolean permProf;
    private Boolean permAssist;
    private String arquivoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DocumentoDto from(Documento d) {
        DocumentoDto dto = new DocumentoDto();
        dto.id = d.getId();
        dto.empresaId = d.getEmpresa() != null ? d.getEmpresa().getId() : null;
        if (d.getPaciente() != null) {
            dto.pacienteId = d.getPaciente().getId();
            dto.pacienteNome = d.getPaciente().getNome();
        }
        if (d.getUsuario() != null) {
            dto.usuarioId = d.getUsuario().getId();
            dto.usuarioNome = d.getUsuario().getNome();
        }
        dto.titulo = d.getTitulo();
        dto.conteudo = d.getConteudo();
        dto.tipo = d.getTipo();
        dto.permProf = d.getPermProf();
        dto.permAssist = d.getPermAssist();
        dto.arquivoUrl = d.getArquivoUrl();
        dto.createdAt = d.getCreatedAt();
        dto.updatedAt = d.getUpdatedAt();
        return dto;
    }
}
