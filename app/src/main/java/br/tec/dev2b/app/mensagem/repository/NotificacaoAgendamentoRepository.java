package br.tec.dev2b.app.mensagem.repository;

import br.tec.dev2b.app.mensagem.model.NotificacaoAgendamento;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificacaoAgendamentoRepository extends JpaRepository<NotificacaoAgendamento, UUID> {

    boolean existsByAgendamentoIdAndTipo(UUID agendamentoId, TipoMensagemPadrao tipo);
}
