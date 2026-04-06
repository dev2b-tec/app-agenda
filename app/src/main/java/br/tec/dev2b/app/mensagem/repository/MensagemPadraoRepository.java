package br.tec.dev2b.app.mensagem.repository;

import br.tec.dev2b.app.mensagem.model.MensagemPadrao;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MensagemPadraoRepository extends JpaRepository<MensagemPadrao, UUID> {

    List<MensagemPadrao> findByEmpresaId(UUID empresaId);

    List<MensagemPadrao> findByEmpresaIdIsNull();

    Optional<MensagemPadrao> findByEmpresaIdAndTipo(UUID empresaId, TipoMensagemPadrao tipo);

    Optional<MensagemPadrao> findByEmpresaIdIsNullAndTipo(TipoMensagemPadrao tipo);
}
