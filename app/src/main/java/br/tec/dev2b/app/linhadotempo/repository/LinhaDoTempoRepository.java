package br.tec.dev2b.app.linhadotempo.repository;

import br.tec.dev2b.app.linhadotempo.model.LinhaDoTempo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinhaDoTempoRepository extends JpaRepository<LinhaDoTempo, UUID> {

    List<LinhaDoTempo> findByPacienteIdOrderByDataDescCreatedAtDesc(UUID pacienteId);

    Optional<LinhaDoTempo> findByReferenciaId(UUID referenciaId);

    void deleteByReferenciaId(UUID referenciaId);
}
