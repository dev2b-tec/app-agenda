package br.tec.dev2b.app.credito.dto;

import br.tec.dev2b.app.credito.model.DadosPagamento;
import lombok.Data;

import java.util.UUID;

@Data
public class DadosPagamentoDto {
    private UUID id;
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
    private String numeroCartaoMascarado;
    private String expiracao;

    public static DadosPagamentoDto from(DadosPagamento d) {
        DadosPagamentoDto dto = new DadosPagamentoDto();
        dto.id = d.getId();
        dto.usuarioId = d.getUsuario() != null ? d.getUsuario().getId() : null;
        dto.nomeCompleto = d.getNomeCompleto();
        dto.email = d.getEmail();
        dto.pais = d.getPais();
        dto.numeroTelefone = d.getNumeroTelefone();
        dto.tipoDocumento = d.getTipoDocumento();
        dto.numeroDocumento = d.getNumeroDocumento();
        dto.logradouro = d.getLogradouro();
        dto.complemento = d.getComplemento();
        dto.bairro = d.getBairro();
        dto.cep = d.getCep();
        dto.metodoPagamento = d.getMetodoPagamento();
        dto.nomeCartao = d.getNomeCartao();
        dto.numeroCartaoMascarado = mascaraCartao(d.getNumeroCartao());
        dto.expiracao = d.getExpiracao();
        return dto;
    }

    private static String mascaraCartao(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }
}
