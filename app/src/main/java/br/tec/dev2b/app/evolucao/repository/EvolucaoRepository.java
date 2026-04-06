package br.tec.dev2b.app.evolucao.repository;

import br.tec.dev2b.app.evolucao.model.Evolucao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EvolucaoRepository extends JpaRepository<Evolucao, UUID> {
    List<Evolucao> findByPacienteIdOrderByDataDesc(UUID pacienteId);
    long countByPacienteEmpresaIdAndDataBetween(UUID empresaId, LocalDate inicio, LocalDate fim);
}
