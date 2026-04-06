package br.tec.dev2b.app.credito.repository;

import br.tec.dev2b.app.credito.model.HistoricoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoCreditoRepository extends JpaRepository<HistoricoCredito, UUID> {
    List<HistoricoCredito> findByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);
    
    @Query("SELECT COALESCE(SUM(h.quantidade), 0) FROM HistoricoCredito h WHERE h.usuario.id = :usuarioId AND h.status = 'CONFIRMADO'")
    Integer calcularSaldoCreditos(UUID usuarioId);
}
