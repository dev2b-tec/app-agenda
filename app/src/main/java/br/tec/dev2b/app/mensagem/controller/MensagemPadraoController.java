package br.tec.dev2b.app.mensagem.controller;

import br.tec.dev2b.app.mensagem.dto.MensagemPadraoDto;
import br.tec.dev2b.app.mensagem.dto.SalvarMensagemPadraoDto;
import br.tec.dev2b.app.mensagem.model.TipoMensagemPadrao;
import br.tec.dev2b.app.mensagem.service.MensagemPadraoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mensagens-padrao")
@RequiredArgsConstructor
public class MensagemPadraoController {

    private final MensagemPadraoService service;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<MensagemPadraoDto>> listar(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(service.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<MensagemPadraoDto> buscarPorTipo(
            @PathVariable UUID empresaId,
            @PathVariable TipoMensagemPadrao tipo) {
        return ResponseEntity.ok(service.buscarPorEmpresaETipo(empresaId, tipo));
    }

    @PutMapping("/empresa/{empresaId}/tipo/{tipo}")
    public ResponseEntity<MensagemPadraoDto> salvar(
            @PathVariable UUID empresaId,
            @PathVariable TipoMensagemPadrao tipo,
            @RequestBody SalvarMensagemPadraoDto dto) {
        return ResponseEntity.ok(service.salvar(empresaId, tipo, dto));
    }
}
