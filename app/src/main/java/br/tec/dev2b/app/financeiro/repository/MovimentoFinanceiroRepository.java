package br.tec.dev2b.app.financeiro.repository;

import br.tec.dev2b.app.financeiro.model.MovimentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovimentoFinanceiroRepository extends JpaRepository<MovimentoFinanceiro, UUID> {

    List<MovimentoFinanceiro> findByEmpresaIdOrderByDataVencimentoDesc(UUID empresaId);

    List<MovimentoFinanceiro> findByEmpresaIdAndTipoOrderByDataVencimentoDesc(UUID empresaId, String tipo);

    List<MovimentoFinanceiro> findByPacienteIdOrderByDataVencimentoDesc(UUID pacienteId);

    List<MovimentoFinanceiro> findByGrupoIdOrderByNumeroParcela(UUID grupoId);

    void deleteByGrupoId(UUID grupoId);

    Optional<MovimentoFinanceiro> findByReferenciaId(UUID referenciaId);

    void deleteByReferenciaId(UUID referenciaId);

    @Query("SELECT DISTINCT m.paciente.id FROM MovimentoFinanceiro m " +
           "WHERE m.empresa.id = :empresaId " +
           "AND m.status = 'EM_ABERTO' " +
           "AND m.dataVencimento < :hoje " +
           "AND m.paciente IS NOT NULL")
    List<UUID> findPacientesInadimplentes(@Param("empresaId") UUID empresaId,
                                          @Param("hoje") LocalDate hoje);
}
