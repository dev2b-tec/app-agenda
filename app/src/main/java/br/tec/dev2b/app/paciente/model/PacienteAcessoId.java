package br.tec.dev2b.app.paciente.model;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteAcessoId implements Serializable {

    private UUID paciente;
    private UUID usuario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacienteAcessoId other)) return false;
        return Objects.equals(paciente, other.paciente) && Objects.equals(usuario, other.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paciente, usuario);
    }
}
