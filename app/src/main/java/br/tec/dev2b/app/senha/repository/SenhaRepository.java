package br.tec.dev2b.app.senha.repository;

import br.tec.dev2b.app.senha.model.Senha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SenhaRepository extends JpaRepository<Senha, UUID> {
    List<Senha> findByUsuarioId(UUID usuarioId);
    List<Senha> findByUsuarioIdAndTipo(UUID usuarioId, String tipo);
}
