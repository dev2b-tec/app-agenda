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

    /**
     * Agendamentos futuros elegíveis para disparo automático de confirmação.
     * Exclui BLOQUEIO, Cancelado, Atendido e Faltou.
     * Carrega paciente e empresa em JOIN para evitar N+1.
     */
    @Query("""
        SELECT a FROM Agendamento a
        LEFT JOIN FETCH a.paciente
        JOIN FETCH a.empresa
        WHERE a.inicio > :agora
          AND a.inicio <= :limite
          AND a.tipo = 'AGENDAMENTO'
          AND a.status NOT IN ('Cancelado', 'Atendido', 'Faltou')
        ORDER BY a.inicio ASC
    """)
    List<Agendamento> findAgendamentosParaDisparo(
            @Param("agora") LocalDateTime agora,
            @Param("limite") LocalDateTime limite);

    /**
     * Agendamentos passados elegíveis para remarcação.
     * Exclui BLOQUEIO, Confirmado, Atendido, Desmarcado, Cancelado.
     * Carrega paciente e empresa em JOIN para evitar N+1.
     */
    @Query("""
        SELECT a FROM Agendamento a
        LEFT JOIN FETCH a.paciente
        LEFT JOIN FETCH a.usuario
        JOIN FETCH a.empresa
        WHERE a.inicio < :agora
          AND a.tipo = 'AGENDAMENTO'
          AND a.status NOT IN ('Confirmado', 'Atendido', 'Desmarcado', 'Cancelado')
        ORDER BY a.inicio DESC
    """)
    List<Agendamento> findAgendamentosParaRemarcacao(
            @Param("agora") LocalDateTime agora);
}
