package br.tec.dev2b.app.paciente.dto;

import br.tec.dev2b.app.paciente.model.Paciente;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PacienteDto {
    private UUID id;
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
    private String statusPagamento;
    private Integer sessoes;
    private String fotoUrl;
    private UUID usuarioId;
    private String usuarioNome;

    public static PacienteDto from(Paciente p) {
        PacienteDto dto = new PacienteDto();
        dto.id = p.getId();
        dto.empresaId = p.getEmpresa() != null ? p.getEmpresa().getId() : null;
        dto.nome = p.getNome();
        dto.dataNascimento = p.getDataNascimento();
        dto.telefone = p.getTelefone();
        dto.genero = p.getGenero();
        dto.plano = p.getPlano();
        dto.numeroCarteirinha = p.getNumeroCarteirinha();
        dto.grupo = p.getGrupo();
        dto.comoConheceu = p.getComoConheceu();
        dto.rg = p.getRg();
        dto.cpf = p.getCpf();
        dto.cep = p.getCep();
        dto.email = p.getEmail();
        dto.logradouro = p.getLogradouro();
        dto.numero = p.getNumero();
        dto.complemento = p.getComplemento();
        dto.bairro = p.getBairro();
        dto.cidade = p.getCidade();
        dto.outrasInformacoes = p.getOutrasInformacoes();
        dto.nomeResponsavel = p.getNomeResponsavel();
        dto.dataNascimentoResp = p.getDataNascimentoResp();
        dto.cpfResponsavel = p.getCpfResponsavel();
        dto.telefoneResponsavel = p.getTelefoneResponsavel();
        dto.statusPagamento = p.getStatusPagamento();
        dto.sessoes = p.getSessoes();
        dto.fotoUrl = p.getFotoUrl();
        if (p.getUsuario() != null) {
            dto.usuarioId = p.getUsuario().getId();
            dto.usuarioNome = p.getUsuario().getNome();
        }
        return dto;
    }
}
