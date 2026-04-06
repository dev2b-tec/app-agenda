package br.tec.dev2b.app.paciente.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CriarPacienteDto {
    private UUID empresaId;
    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private String genero;
    private String plano;
    private String numeroCarteirinha;
    private String grupo;
    private String comoConheceu;
    private String rg;
    private String cpf;
    private String cep;
    private String email;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String outrasInformacoes;
    private String nomeResponsavel;
    private LocalDate dataNascimentoResp;
    private String cpfResponsavel;
    private String telefoneResponsavel;
}
