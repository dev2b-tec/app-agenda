package br.tec.dev2b.app.credito.controller;

import br.tec.dev2b.app.credito.dto.ComprarCreditosDto;
import br.tec.dev2b.app.credito.dto.DadosPagamentoDto;
import br.tec.dev2b.app.credito.dto.HistoricoCreditoDto;
import br.tec.dev2b.app.credito.service.CreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/creditos")
@RequiredArgsConstructor
public class CreditoController {

    private final CreditoService creditoService;

    @PostMapping("/comprar")
    public ResponseEntity<HistoricoCreditoDto> comprarCreditos(@RequestBody ComprarCreditosDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditoService.comprarCreditos(dto));
    }

    @GetMapping("/historico/usuario/{usuarioId}")
    public ResponseEntity<List<HistoricoCreditoDto>> listarHistorico(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(creditoService.listarHistorico(usuarioId));
    }

    @GetMapping("/saldo/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Integer>> calcularSaldo(@PathVariable UUID usuarioId) {
        Integer saldo = creditoService.calcularSaldo(usuarioId);
        return ResponseEntity.ok(Map.of("saldo", saldo));
    }

    @GetMapping("/dados-pagamento/usuario/{usuarioId}")
    public ResponseEntity<DadosPagamentoDto> obterUltimosDadosPagamento(@PathVariable UUID usuarioId) {
        DadosPagamentoDto dados = creditoService.obterUltimosDadosPagamento(usuarioId);
        if (dados == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dados);
    }
}
