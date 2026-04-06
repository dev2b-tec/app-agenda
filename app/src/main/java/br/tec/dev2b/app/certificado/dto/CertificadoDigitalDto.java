package br.tec.dev2b.app.certificado.dto;

import br.tec.dev2b.app.certificado.model.CertificadoDigital;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CertificadoDigitalDto {
    private UUID id;
    private UUID empresaId;
    private String nomeArquivo;
    private String urlMinio;
    private Long tamanhoBytes;
    private LocalDate dataValidade;
    private Boolean ativo;

    public static CertificadoDigitalDto from(CertificadoDigital cert) {
        CertificadoDigitalDto dto = new CertificadoDigitalDto();
        dto.id = cert.getId();
        dto.empresaId = cert.getEmpresa() != null ? cert.getEmpresa().getId() : null;
        dto.nomeArquivo = cert.getNomeArquivo();
        dto.urlMinio = cert.getUrlMinio();
        dto.tamanhoBytes = cert.getTamanhoBytes();
        dto.dataValidade = cert.getDataValidade();
        dto.ativo = cert.getAtivo();
        return dto;
    }
}
