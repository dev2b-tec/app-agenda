package br.tec.dev2b.app.empresa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarEmpresaDto {

    private String nomeComercial;
    private String telefoneComercial;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private Integer duracaoSessaoMinutos;
}
