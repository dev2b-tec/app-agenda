package br.tec.dev2b.app.credito.repository;

import br.tec.dev2b.app.credito.model.DadosPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DadosPagamentoRepository extends JpaRepository<DadosPagamento, UUID> {
    Optional<DadosPagamento> findFirstByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);
}
