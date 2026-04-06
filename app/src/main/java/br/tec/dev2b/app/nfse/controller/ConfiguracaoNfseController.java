package br.tec.dev2b.app.nfse.controller;

import br.tec.dev2b.app.nfse.dto.ConfiguracaoNfseDto;
import br.tec.dev2b.app.nfse.dto.ConsultarCnpjDto;
import br.tec.dev2b.app.nfse.service.ConfiguracaoNfseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/configuracoes-nfse")
@RequiredArgsConstructor
public class ConfiguracaoNfseController {

    private final ConfiguracaoNfseService configuracaoNfseService;

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<ConfiguracaoNfseDto> buscarPorEmpresa(@PathVariable UUID empresaId) {
        ConfiguracaoNfseDto config = configuracaoNfseService.buscarPorEmpresa(empresaId);
        if (config == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(config);
    }

    @PostMapping("/consultar-cnpj")
    public ResponseEntity<ConfiguracaoNfseDto> consultarEValidarCnpj(@RequestBody ConsultarCnpjDto dto) {
        try {
            ConfiguracaoNfseDto config = configuracaoNfseService.consultarEValidarCnpj(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(config);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/empresa/{empresaId}")
    public ResponseEntity<Void> deletar(@PathVariable UUID empresaId) {
        configuracaoNfseService.deletar(empresaId);
        return ResponseEntity.noContent().build();
    }
}
