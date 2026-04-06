package br.tec.dev2b.app.sala.repository;

import br.tec.dev2b.app.sala.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalaRepository extends JpaRepository<Sala, UUID> {
    List<Sala> findByEmpresaId(UUID empresaId);
    List<Sala> findByEmpresaIdAndAtivaTrue(UUID empresaId);
}
