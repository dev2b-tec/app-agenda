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

    List<Paciente> findByEmpresaIdAndStatusPagamentoOrderByNomeAsc(UUID empresaId, String statusPagamento);
    
    /** Lista apenas pacientes vinculados ao usuário via paciente_acessos */
    @Query("""
            SELECT DISTINCT p FROM Paciente p
            WHERE p.empresa.id = :empresaId
              AND EXISTS (SELECT 1 FROM PacienteAcesso a WHERE a.paciente = p AND a.usuario.id = :usuarioId)
            ORDER BY p.nome ASC
            """)
    List<Paciente> findByEmpresaIdAndUsuarioIdOrderByNomeAsc(
            @Param("empresaId") UUID empresaId,
            @Param("usuarioId") UUID usuarioId);

    @Query("SELECT p FROM Paciente p WHERE p.empresa.id = :empresaId AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(p.telefone) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(p.cpf) LIKE LOWER(CONCAT('%', :busca, '%'))) " +
           "ORDER BY p.nome ASC")
    List<Paciente> buscarPorEmpresaETexto(@Param("empresaId") UUID empresaId, @Param("busca") String busca);

    /** Busca filtrando apenas por usuário via paciente_acessos */
    @Query("""
            SELECT DISTINCT p FROM Paciente p
            WHERE p.empresa.id = :empresaId
              AND EXISTS (SELECT 1 FROM PacienteAcesso a WHERE a.paciente = p AND a.usuario.id = :usuarioId)
              AND (LOWER(p.nome) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(p.telefone) LIKE LOWER(CONCAT('%', :busca, '%'))
                   OR LOWER(p.cpf) LIKE LOWER(CONCAT('%', :busca, '%')))
            ORDER BY p.nome ASC
            """)
    List<Paciente> buscarPorEmpresaUsuarioETexto(
            @Param("empresaId") UUID empresaId,
            @Param("usuarioId") UUID usuarioId,
            @Param("busca") String busca);
}
