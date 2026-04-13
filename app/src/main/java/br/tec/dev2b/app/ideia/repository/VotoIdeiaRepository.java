package br.tec.dev2b.app.ideia.repository;

import br.tec.dev2b.app.ideia.model.VotoIdeia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VotoIdeiaRepository extends JpaRepository<VotoIdeia, UUID> {

    long countByIdeiaId(UUID ideiaId);

    Optional<VotoIdeia> findByIdeiaIdAndEmpresaId(UUID ideiaId, UUID empresaId);

    boolean existsByIdeiaIdAndEmpresaId(UUID ideiaId, UUID empresaId);
}
