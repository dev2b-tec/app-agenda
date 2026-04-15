package br.tec.dev2b.app.marcador.repository;

import br.tec.dev2b.app.marcador.model.Marcador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarcadorRepository extends JpaRepository<Marcador, UUID> {
    List<Marcador> findByEmpresaId(UUID empresaId);
    Optional<Marcador> findByEmpresaIdAndTipo(UUID empresaId, String tipo);
    boolean existsByEmpresaIdAndTipo(UUID empresaId, String tipo);
}
