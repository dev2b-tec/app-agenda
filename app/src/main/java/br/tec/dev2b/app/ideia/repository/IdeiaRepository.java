package br.tec.dev2b.app.ideia.repository;

import br.tec.dev2b.app.ideia.model.Ideia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdeiaRepository extends JpaRepository<Ideia, UUID> {

    Page<Ideia> findAllByOrderByNumeroDesc(Pageable pageable);

    Page<Ideia> findBySituacaoOrderByNumeroDesc(String situacao, Pageable pageable);

    Page<Ideia> findByEmpresaIdOrderByNumeroDesc(UUID empresaId, Pageable pageable);

    Page<Ideia> findBySituacaoInOrderByNumeroDesc(java.util.List<String> situacoes, Pageable pageable);
}
