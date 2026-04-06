package br.tec.dev2b.app.mensagem.service;

import br.tec.dev2b.app.mensagem.dto.MensagemPadraoDto;
import br.tec.dev2b.app.mensagem.dto.SalvarMensagemPadraoDto;
import br.tec.dev2b.app.mensagem.model.MensagemPadrao;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import br.tec.dev2b.app.mensagem.repository.MensagemPadraoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensagemPadraoService {

    private final MensagemPadraoRepository repository;

    /**
     * Lista todas as mensagens para a empresa, preenchendo com os defaults globais
     * para os tipos que ainda não foram personalizados.
     */
    @Transactional(readOnly = true)
    public List<MensagemPadraoDto> listarPorEmpresa(UUID empresaId) {
        Map<TipoMensagemPadrao, MensagemPadrao> empresa =
                repository.findByEmpresaId(empresaId).stream()
                        .collect(Collectors.toMap(MensagemPadrao::getTipo, m -> m));

        Map<TipoMensagemPadrao, MensagemPadrao> defaults =
                repository.findByEmpresaIdIsNull().stream()
                        .collect(Collectors.toMap(MensagemPadrao::getTipo, m -> m));

        return Arrays.stream(TipoMensagemPadrao.values())
                .map(tipo -> {
                    MensagemPadrao m = empresa.getOrDefault(tipo, defaults.get(tipo));
                    if (m == null) return null;
                    return MensagemPadraoDto.from(m);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Retorna a mensagem da empresa para o tipo, caindo para o default global se
     * não existir um registro específico.
     */
    @Transactional(readOnly = true)
    public MensagemPadraoDto buscarPorEmpresaETipo(UUID empresaId, TipoMensagemPadrao tipo) {
        return repository.findByEmpresaIdAndTipo(empresaId, tipo)
                .or(() -> repository.findByEmpresaIdIsNullAndTipo(tipo))
                .map(MensagemPadraoDto::from)
                .orElseThrow(() -> new NoSuchElementException("Mensagem padrão não encontrada para tipo: " + tipo));
    }

    /**
     * Upsert da mensagem da empresa para o tipo.
     */
    @Transactional
    public MensagemPadraoDto salvar(UUID empresaId, TipoMensagemPadrao tipo, SalvarMensagemPadraoDto dto) {
        MensagemPadrao msg = repository.findByEmpresaIdAndTipo(empresaId, tipo)
                .orElse(MensagemPadrao.builder()
                        .empresaId(empresaId)
                        .tipo(tipo)
                        .isDefault(false)
                        .build());

        msg.setTexto(dto.getTexto());
        return MensagemPadraoDto.from(repository.save(msg));
    }
}
