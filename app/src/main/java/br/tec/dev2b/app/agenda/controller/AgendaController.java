package br.tec.dev2b.app.agenda.controller;

import br.tec.dev2b.app.agenda.dto.AgendaDto;
import br.tec.dev2b.app.agenda.dto.AtualizarAgendaDto;
import br.tec.dev2b.app.agenda.service.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping
    public ResponseEntity<AgendaDto> criar() {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.criar());
    }

    @GetMapping
    public ResponseEntity<List<AgendaDto>> listar() {
        return ResponseEntity.ok(agendaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(agendaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendaDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarAgendaDto dto) {
        return ResponseEntity.ok(agendaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        agendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
