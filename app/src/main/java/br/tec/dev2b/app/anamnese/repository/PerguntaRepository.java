package br.tec.dev2b.app.anamnese.repository;

import br.tec.dev2b.app.anamnese.model.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, UUID> {
}
