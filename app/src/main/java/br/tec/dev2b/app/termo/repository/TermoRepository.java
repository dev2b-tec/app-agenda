package br.tec.dev2b.app.termo.repository;

import br.tec.dev2b.app.termo.model.Termo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermoRepository extends JpaRepository<Termo, UUID> {
    
    Optional<Termo> findByTipoAndAtivoTrue(String tipo);
}
