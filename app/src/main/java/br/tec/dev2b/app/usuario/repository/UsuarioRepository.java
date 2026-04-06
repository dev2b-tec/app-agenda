package br.tec.dev2b.app.usuario.repository;

import br.tec.dev2b.app.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByKeycloakId(String keycloakId);

    boolean existsByEmail(String email);

    List<Usuario> findByEmpresaId(UUID empresaId);
}
