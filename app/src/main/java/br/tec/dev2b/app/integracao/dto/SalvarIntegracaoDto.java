package br.tec.dev2b.app.integracao.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SalvarIntegracaoDto {
    private Boolean ativo;
    private Map<String, String> configuracao;
}
