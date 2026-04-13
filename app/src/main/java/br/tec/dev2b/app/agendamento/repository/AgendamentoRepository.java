package br.tec.dev2b.app.agendamento.repository;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {

    List<Agendamento> findByEmpresaIdOrderByInicioAsc(UUID empresaId);

    Optional<Agendamento> findByWherebyMeetingId(String wherebyMeetingId);

    @Query("""
        SELECT a FROM Agendamento a
        WHERE a.usuario.id = :usuarioId
          AND a.status NOT IN ('Cancelado', 'Faltou')
          AND a.inicio < :fim
          AND a.fim > :inicio
    """)
    List<Agendamento> findConflitos(
            @Param("usuarioId") UUID usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    @Query("""
        SELECT a FROM Agendamento a
        WHERE a.empresa.id = :empresaId
          AND a.inicio >= :inicio
          AND a.inicio < :fim
        ORDER BY a.inicio ASC
    """)
    List<Agendamento> findByEmpresaIdAndPeriodo(
            @Param("empresaId") UUID empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}
