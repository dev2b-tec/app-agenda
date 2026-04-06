package br.tec.dev2b.app.cnae.repository;

import br.tec.dev2b.app.cnae.model.Cnae;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CnaeRepository extends JpaRepository<Cnae, UUID> {
    List<Cnae> findByEmpresaIdOrderByCodigoCnaeAsc(UUID empresaId);
    List<Cnae> findByEmpresaIdAndAtivoTrueOrderByCodigoCnaeAsc(UUID empresaId);
}
