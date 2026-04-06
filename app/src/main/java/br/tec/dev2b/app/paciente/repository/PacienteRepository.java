package br.tec.dev2b.app.paciente.repository;

import br.tec.dev2b.app.paciente.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    List<Paciente> findByEmpresaIdOrderByNomeAsc(UUID empresaId);
    
    @Query("SELECT p FROM Paciente p WHERE p.empresa.id = :empresaId AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(p.telefone) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(p.cpf) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
           "ORDER BY p.nome ASC")
    List<Paciente> buscarPorEmpresaETexto(@Param("empresaId") UUID empresaId, @Param("busca") String busca);
    
    List<Paciente> findByEmpresaIdAndStatusPagamentoOrderByNomeAsc(UUID empresaId, String statusPagamento);

    @Query("SELECT p FROM Paciente p WHERE p.empresa.id = :empresaId " +
           "AND p.dataNascimento = :dataNascimento " +
           "AND p.telefone LIKE CONCAT('%', :telefone, '%')")
    Optional<Paciente> findByEmpresaIdAndDataNascimentoAndTelefone(
            @Param("empresaId") UUID empresaId,
            @Param("dataNascimento") LocalDate dataNascimento,
            @Param("telefone") String telefone);
}
