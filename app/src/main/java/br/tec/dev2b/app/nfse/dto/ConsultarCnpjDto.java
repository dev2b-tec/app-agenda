package br.tec.dev2b.app.nfse.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConsultarCnpjDto {
    private UUID empresaId;
    private String cnpj;
}
