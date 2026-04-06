package br.tec.dev2b.app.usuario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SyncUsuarioDto {

    private String nome;
    private String email;
    private String keycloakId;
}
