package br.tec.dev2b.app.usuario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CriarUsuarioDto {

    private String nome;
    private String email;
    private String keycloakId;
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
    private String genero;
    private String tipoAcesso;
    private Long perfilId;
}
