package br.tec.dev2b.app.credito.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CriarDadosPagamentoDto {
    private UUID usuarioId;
    private String nomeCompleto;
    private String email;
    private String pais;
    private String numeroTelefone;
    private String tipoDocumento;
    private String numeroDocumento;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String cep;
    private String metodoPagamento;
    private String nomeCartao;
    private String numeroCartao;
    private String cvv;
    private String expiracao;
}
