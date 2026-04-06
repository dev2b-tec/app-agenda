package br.tec.dev2b.app.agendamento.controller;

import br.tec.dev2b.app.agendamento.dto.AgendamentoDto;
import br.tec.dev2b.app.agendamento.dto.AtualizarAgendamentoDto;
import br.tec.dev2b.app.agendamento.dto.CriarAgendamentoDto;
import br.tec.dev2b.app.agendamento.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping
    public ResponseEntity<AgendamentoDto> criar(@RequestBody CriarAgendamentoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<AgendamentoDto>> listarPorEmpresa(
            @PathVariable UUID empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        if (inicio != null && fim != null) {
            return ResponseEntity.ok(service.listarPorEmpresaEPeriodo(empresaId, inicio, fim));
        }
        return ResponseEntity.ok(service.listarPorEmpresa(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoDto> atualizar(
            @PathVariable UUID id,
            @RequestBody AtualizarAgendamentoDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
