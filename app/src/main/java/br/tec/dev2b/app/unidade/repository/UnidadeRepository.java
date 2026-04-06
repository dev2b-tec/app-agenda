package br.tec.dev2b.app.unidade.repository;

import br.tec.dev2b.app.unidade.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {
    List<Unidade> findByEmpresaId(UUID empresaId);
    List<Unidade> findByEmpresaIdAndAtivaTrue(UUID empresaId);
}
