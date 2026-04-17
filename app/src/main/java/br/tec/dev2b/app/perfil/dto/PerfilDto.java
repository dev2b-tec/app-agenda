package br.tec.dev2b.app.perfil.dto;

import br.tec.dev2b.app.perfil.model.Perfil;
import br.tec.dev2b.app.perfil.model.PerfilPermissao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PerfilDto {

    private Long id;
    private String nome;
    private String descricao;
    private List<String> permissoes;

    public static PerfilDto from(Perfil p) {
        PerfilDto dto = new PerfilDto();
        dto.id = p.getId();
        dto.nome = p.getNome();
        dto.descricao = p.getDescricao();
        dto.permissoes = p.getPermissoes().stream()
                .map(PerfilPermissao::getPermissao)
                .toList();
        return dto;
    }
}
