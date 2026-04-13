package br.tec.dev2b.app.ideia.repository;

import br.tec.dev2b.app.ideia.model.ComentarioIdeia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ComentarioIdeiaRepository extends JpaRepository<ComentarioIdeia, UUID> {

    List<ComentarioIdeia> findByIdeiaIdOrderByCreatedAtAsc(UUID ideiaId);

    long countByIdeiaId(UUID ideiaId);
}
