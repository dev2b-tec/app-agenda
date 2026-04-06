package br.tec.dev2b.app.nfse.dto;

import br.tec.dev2b.app.nfse.model.ConfiguracaoNfse;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfiguracaoNfseDto {
    private UUID id;
    private UUID empresaId;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String dataAbertura;
    private String situacao;
    private String uf;
    private String municipio;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String email;
    private String telefone;
    private Boolean validadoReceita;

    public static ConfiguracaoNfseDto from(ConfiguracaoNfse c) {
        ConfiguracaoNfseDto dto = new ConfiguracaoNfseDto();
        dto.id = c.getId();
        dto.empresaId = c.getEmpresa() != null ? c.getEmpresa().getId() : null;
        dto.cnpj = c.getCnpj();
        dto.razaoSocial = c.getRazaoSocial();
        dto.nomeFantasia = c.getNomeFantasia();
        dto.dataAbertura = c.getDataAbertura();
        dto.situacao = c.getSituacao();
        dto.uf = c.getUf();
        dto.municipio = c.getMunicipio();
        dto.logradouro = c.getLogradouro();
        dto.numero = c.getNumero();
        dto.complemento = c.getComplemento();
        dto.bairro = c.getBairro();
        dto.cep = c.getCep();
        dto.email = c.getEmail();
        dto.telefone = c.getTelefone();
        dto.validadoReceita = c.getValidadoReceita();
        return dto;
    }
}
