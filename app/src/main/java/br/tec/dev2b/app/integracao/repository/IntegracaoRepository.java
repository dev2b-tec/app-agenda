package br.tec.dev2b.app.integracao.repository;

import br.tec.dev2b.app.integracao.model.Integracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntegracaoRepository extends JpaRepository<Integracao, UUID> {
    List<Integracao> findByEmpresaId(UUID empresaId);
    Optional<Integracao> findByEmpresaIdAndTipo(UUID empresaId, String tipo);
}
