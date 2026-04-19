package br.tec.dev2b.app.paciente.repository;

import br.tec.dev2b.app.paciente.model.PacienteAcesso;
import br.tec.dev2b.app.paciente.model.PacienteAcessoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PacienteAcessoRepository extends JpaRepository<PacienteAcesso, PacienteAcessoId> {

    @Query("SELECT a FROM PacienteAcesso a JOIN FETCH a.usuario WHERE a.paciente.id = :pacienteId")
    List<PacienteAcesso> findByPacienteId(@Param("pacienteId") UUID pacienteId);

    boolean existsByPacienteIdAndUsuarioId(UUID pacienteId, UUID usuarioId);

    void deleteByPacienteIdAndUsuarioId(UUID pacienteId, UUID usuarioId);
}
