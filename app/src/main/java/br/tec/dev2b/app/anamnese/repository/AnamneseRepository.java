package br.tec.dev2b.app.anamnese.repository;

import br.tec.dev2b.app.anamnese.model.Anamnese;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnamneseRepository extends JpaRepository<Anamnese, UUID> {
    List<Anamnese> findByEmpresaId(UUID empresaId);
}
