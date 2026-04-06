package br.tec.dev2b.app.documento.repository;

import br.tec.dev2b.app.documento.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {
    List<Documento> findByPacienteIdOrderByCreatedAtDesc(UUID pacienteId);
}
