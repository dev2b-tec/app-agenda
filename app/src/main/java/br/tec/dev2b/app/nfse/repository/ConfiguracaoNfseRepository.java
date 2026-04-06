package br.tec.dev2b.app.nfse.repository;

import br.tec.dev2b.app.nfse.model.ConfiguracaoNfse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoNfseRepository extends JpaRepository<ConfiguracaoNfse, UUID> {
    Optional<ConfiguracaoNfse> findByEmpresaId(UUID empresaId);
}
