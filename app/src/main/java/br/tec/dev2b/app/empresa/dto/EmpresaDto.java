package br.tec.dev2b.app.empresa.dto;

import br.tec.dev2b.app.empresa.model.Empresa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDto {

    private UUID id;
    private String nomeComercial;
    private String logoUrl;
    private String telefoneComercial;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private Integer duracaoSessaoMinutos;
    private UUID agendaId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmpresaDto from(Empresa e) {
        EmpresaDto dto = new EmpresaDto();
        dto.id = e.getId();
        dto.nomeComercial = e.getNomeComercial();
        dto.logoUrl = e.getLogoUrl();
        dto.telefoneComercial = e.getTelefoneComercial();
        dto.cep = e.getCep();
        dto.logradouro = e.getLogradouro();
        dto.numero = e.getNumero();
        dto.complemento = e.getComplemento();
        dto.bairro = e.getBairro();
        dto.cidade = e.getCidade();
        dto.duracaoSessaoMinutos = e.getDuracaoSessaoMinutos();
        dto.agendaId = e.getAgenda() != null ? e.getAgenda().getId() : null;
        dto.createdAt = e.getCreatedAt();
        dto.updatedAt = e.getUpdatedAt();
        return dto;
    }
}
