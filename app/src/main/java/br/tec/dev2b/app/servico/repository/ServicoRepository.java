package br.tec.dev2b.app.servico.repository;

import br.tec.dev2b.app.servico.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, UUID> {
    List<Servico> findByEmpresaId(UUID empresaId);
    List<Servico> findByEmpresaIdAndAtivoTrue(UUID empresaId);
    List<Servico> findByEmpresaIdAndTipo(UUID empresaId, String tipo);
}
