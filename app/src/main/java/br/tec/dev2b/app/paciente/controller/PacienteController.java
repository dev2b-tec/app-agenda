package br.tec.dev2b.app.paciente.controller;

import br.tec.dev2b.app.paciente.dto.AtualizarPacienteDto;
import br.tec.dev2b.app.paciente.dto.CriarPacienteDto;
import br.tec.dev2b.app.paciente.dto.PacienteDto;
import br.tec.dev2b.app.paciente.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<PacienteDto> criar(@RequestBody CriarPacienteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<PacienteDto>> listarPorEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(pacienteService.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/buscar")
    public ResponseEntity<List<PacienteDto>> buscar(
            @PathVariable UUID empresaId,
            @RequestParam(required = false) String q) {
        return ResponseEntity.ok(pacienteService.buscar(empresaId, q));
    }

    @GetMapping("/empresa/{empresaId}/status/{status}")
    public ResponseEntity<List<PacienteDto>> filtrarPorStatus(
            @PathVariable UUID empresaId,
            @PathVariable String status) {
        return ResponseEntity.ok(pacienteService.filtrarPorStatus(empresaId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PacienteDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarPacienteDto dto) {
        return ResponseEntity.ok(pacienteService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PacienteDto> uploadFoto(
            @PathVariable UUID id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(pacienteService.uploadFoto(id, arquivo));
    }
}
