package br.tec.dev2b.app.usuario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AtualizarUsuarioDto {

    private String nome;
    private String telefone;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String tipo;
    private String conselho;
    private String numeroConselho;
    private String especialidade;
    private UUID empresaId;
    private UUID agendaId;
    private String genero;
    private String tipoAcesso;
    private Integer duracaoSessao;
    private String periodoMinimo;
    private String periodoMaximo;
    private String tempoAntecedencia;
    private Boolean disponivel;
    private String telefoneComercial;
    private String observacoes;

    // Buscador DEV2B
    private String descricaoAtuacao;
    private String cursosCertificacoes;
    private String faixaPreco;
    private String modoAtendimento;
    private Boolean aceitaConvenio;
    private String instagram;
    private String facebook;
    private String linkedin;
    private String youtube;
    private String websiteLink;
    private String whatsapp;
    private Boolean publicadoBuscador;

    private Long perfilId;
}
