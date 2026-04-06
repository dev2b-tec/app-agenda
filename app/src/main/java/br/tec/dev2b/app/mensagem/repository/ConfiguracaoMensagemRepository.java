package br.tec.dev2b.app.mensagem.repository;

import br.tec.dev2b.app.mensagem.model.ConfiguracaoMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoMensagemRepository extends JpaRepository<ConfiguracaoMensagem, UUID> {
    Optional<ConfiguracaoMensagem> findByUsuarioId(UUID usuarioId);
}
