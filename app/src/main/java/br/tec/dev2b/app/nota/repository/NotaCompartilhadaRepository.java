package br.tec.dev2b.app.nota.repository;

import br.tec.dev2b.app.nota.model.NotaCompartilhada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotaCompartilhadaRepository extends JpaRepository<NotaCompartilhada, UUID> {
    List<NotaCompartilhada> findByPacienteIdOrderByCriadoEmDesc(UUID pacienteId);
}
