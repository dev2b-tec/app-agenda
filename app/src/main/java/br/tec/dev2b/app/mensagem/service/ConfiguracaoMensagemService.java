package br.tec.dev2b.app.mensagem.service;

import br.tec.dev2b.app.mensagem.dto.AtualizarConfiguracaoMensagemDto;
import br.tec.dev2b.app.mensagem.dto.ConfiguracaoMensagemDto;
import br.tec.dev2b.app.mensagem.model.ConfiguracaoMensagem;
import br.tec.dev2b.app.mensagem.model.MensagemPadrao;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import br.tec.dev2b.app.mensagem.repository.ConfiguracaoMensagemRepository;
import br.tec.dev2b.app.mensagem.repository.MensagemPadraoRepository;
import br.tec.dev2b.app.usuario.model.Usuario;
import br.tec.dev2b.app.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfiguracaoMensagemService {

    private final ConfiguracaoMensagemRepository configuracaoMensagemRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensagemPadraoRepository mensagemPadraoRepository;

    @Transactional(readOnly = true)
    public ConfiguracaoMensagemDto buscarPorUsuario(UUID usuarioId) {
        return configuracaoMensagemRepository.findByUsuarioId(usuarioId)
                .map(ConfiguracaoMensagemDto::from)
                .orElse(null);
    }

    @Transactional
    public ConfiguracaoMensagemDto salvarOuAtualizar(UUID usuarioId, AtualizarConfiguracaoMensagemDto dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        ConfiguracaoMensagem config = configuracaoMensagemRepository.findByUsuarioId(usuarioId)
                .orElse(ConfiguracaoMensagem.builder()
                        .usuario(usuario)
                        .build());

        if (dto.getNumeroWhatsapp() != null) config.setNumeroWhatsapp(dto.getNumeroWhatsapp());
        if (dto.getPermitirProfissionais() != null) config.setPermitirProfissionais(dto.getPermitirProfissionais());
        if (dto.getEnvioSmsAutomatico() != null) config.setEnvioSmsAutomatico(dto.getEnvioSmsAutomatico());
        if (dto.getEnviarComRiscoFalta() != null) config.setEnviarComRiscoFalta(dto.getEnviarComRiscoFalta());
        if (dto.getHorarioDisparo() != null) config.setHorarioDisparo(dto.getHorarioDisparo());
        if (dto.getMensagemLembrete() != null) config.setMensagemLembrete(dto.getMensagemLembrete());

        ConfiguracaoMensagemDto result = ConfiguracaoMensagemDto.from(configuracaoMensagemRepository.save(config));

        // Se ativando disparo automático, garante que a empresa tenha mensagens_padrao para todos os tipos
        if (Boolean.TRUE.equals(config.getEnvioSmsAutomatico()) && usuario.getEmpresa() != null) {
            garantirMensagensPadrao(usuario.getEmpresa().getId());
        }

        return result;
    }

    private void garantirMensagensPadrao(UUID empresaId) {
        Map<TipoMensagemPadrao, MensagemPadrao> existentes = mensagemPadraoRepository
                .findByEmpresaId(empresaId)
                .stream()
                .collect(Collectors.toMap(MensagemPadrao::getTipo, m -> m));

        Map<TipoMensagemPadrao, MensagemPadrao> defaults = mensagemPadraoRepository
                .findByEmpresaIdIsNull()
                .stream()
                .collect(Collectors.toMap(MensagemPadrao::getTipo, m -> m));

        Arrays.stream(TipoMensagemPadrao.values()).forEach(tipo -> {
            if (!existentes.containsKey(tipo) && defaults.containsKey(tipo)) {
                MensagemPadrao novo = MensagemPadrao.builder()
                        .empresaId(empresaId)
                        .tipo(tipo)
                        .texto(defaults.get(tipo).getTexto())
                        .isDefault(false)
                        .build();
                mensagemPadraoRepository.save(novo);
            }
        });
    }
}
