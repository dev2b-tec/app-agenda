package br.tec.dev2b.app.infra.whereby;

import br.tec.dev2b.app.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WhereBySessaoRepository extends JpaRepository<WhereBySessao, UUID> {

    Optional<WhereBySessao> findByMeetingIdAndParticipantIdAndEndedAtIsNull(String meetingId, String participantId);

    /** Sessões em aberto cujo agendamento terminou há mais de 2 horas — candidatas a encerramento forçado. */
    @Query("""
        SELECT s FROM WhereBySessao s
        WHERE s.endedAt IS NULL
          AND s.meetingId IN (
              SELECT a.wherebyMeetingId FROM Agendamento a
              WHERE a.fim < :limite AND a.wherebyMeetingId IS NOT NULL
          )
    """)
    List<WhereBySessao> findOrfas(@Param("limite") LocalDateTime limite);

    List<WhereBySessao> findByAgendamentoIdOrderByStartedAtAsc(UUID agendamentoId);

    List<WhereBySessao> findByMeetingIdOrderByStartedAtAsc(String meetingId);
}
