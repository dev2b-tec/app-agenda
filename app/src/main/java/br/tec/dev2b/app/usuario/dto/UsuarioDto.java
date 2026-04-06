package br.tec.dev2b.app.usuario.dto;

import br.tec.dev2b.app.usuario.model.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDto {

    private UUID id;
    private String nome;
    private String email;
    private String keycloakId;
    private String fotoUrl;
    private String assinaturaUrl;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String genero;
    private Integer duracaoSessao;
    private String periodoMinimo;
    private String periodoMaximo;
    private String tempoAntecedencia;
    private Boolean disponivel;
    private String telefoneComercial;
    private String observacoes;

    public static UsuarioDto from(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.id = u.getId();
        dto.nome = u.getNome();
        dto.email = u.getEmail();
        dto.keycloakId = u.getKeycloakId();
        dto.fotoUrl = u.getFotoUrl();
        dto.assinaturaUrl = u.getAssinaturaUrl();
        dto.telefone = u.getTelefone();
        dto.cep = u.getCep();
        dto.logradouro = u.getLogradouro();
        dto.numero = u.getNumero();
        dto.complemento = u.getComplemento();
        dto.bairro = u.getBairro();
        dto.cidade = u.getCidade();
        dto.tipo = u.getTipo();
        dto.conselho = u.getConselho();
        dto.numeroConselho = u.getNumeroConselho();
        dto.especialidade = u.getEspecialidade();
        dto.empresaId = u.getEmpresa() != null ? u.getEmpresa().getId() : null;
        dto.agendaId = u.getAgenda() != null ? u.getAgenda().getId() : null;
        dto.createdAt = u.getCreatedAt();
        dto.updatedAt = u.getUpdatedAt();
        dto.genero = u.getGenero();
        dto.duracaoSessao = u.getDuracaoSessao();
        dto.periodoMinimo = u.getPeriodoMinimo();
        dto.periodoMaximo = u.getPeriodoMaximo();
        dto.tempoAntecedencia = u.getTempoAntecedencia();
        dto.disponivel = u.getDisponivel();
        dto.telefoneComercial = u.getTelefoneComercial();
        dto.observacoes = u.getObservacoes();
        return dto;
    }
}
