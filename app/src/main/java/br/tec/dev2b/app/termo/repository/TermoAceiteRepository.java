package br.tec.dev2b.app.termo.repository;

import br.tec.dev2b.app.termo.model.TermoAceite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermoAceiteRepository extends JpaRepository<TermoAceite, UUID> {
    
    Optional<TermoAceite> findByTermoIdAndEmpresaId(UUID termoId, UUID empresaId);
    
    boolean existsByTermoIdAndEmpresaId(UUID termoId, UUID empresaId);
}
