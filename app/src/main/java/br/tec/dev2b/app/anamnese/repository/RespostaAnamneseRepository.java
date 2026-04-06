package br.tec.dev2b.app.anamnese.repository;

import br.tec.dev2b.app.anamnese.model.RespostaAnamnese;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RespostaAnamneseRepository extends JpaRepository<RespostaAnamnese, UUID> {

    Optional<RespostaAnamnese> findByPacienteIdAndAnamneseId(UUID pacienteId, UUID anamneseId);
}
