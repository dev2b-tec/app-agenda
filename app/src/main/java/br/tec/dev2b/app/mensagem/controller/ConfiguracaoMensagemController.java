package br.tec.dev2b.app.mensagem.controller;

import br.tec.dev2b.app.mensagem.dto.AtualizarConfiguracaoMensagemDto;
import br.tec.dev2b.app.mensagem.dto.ConfiguracaoMensagemDto;
import br.tec.dev2b.app.mensagem.service.ConfiguracaoMensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuracoes-mensagens")
@RequiredArgsConstructor
public class ConfiguracaoMensagemController {

    private final ConfiguracaoMensagemService configuracaoMensagemService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ConfiguracaoMensagemDto> buscarPorUsuario(@PathVariable UUID usuarioId) {
        ConfiguracaoMensagemDto config = configuracaoMensagemService.buscarPorUsuario(usuarioId);
        if (config == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(config);
    }

    @PutMapping("/usuario/{usuarioId}")
    public ResponseEntity<ConfiguracaoMensagemDto> salvarOuAtualizar(
            @PathVariable UUID usuarioId,
            @RequestBody AtualizarConfiguracaoMensagemDto dto) {
        return ResponseEntity.ok(configuracaoMensagemService.salvarOuAtualizar(usuarioId, dto));
    }
}
