package br.tec.dev2b.app.documento.controller;

import br.tec.dev2b.app.documento.dto.AtualizarDocumentoDto;
import br.tec.dev2b.app.documento.dto.CriarDocumentoDto;
import br.tec.dev2b.app.documento.dto.DocumentoDto;
import br.tec.dev2b.app.documento.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<DocumentoDto>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(documentoService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DocumentoDto> criar(@RequestBody CriarDocumentoDto dto) {
        return ResponseEntity.ok(documentoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarDocumentoDto dto) {
        return ResponseEntity.ok(documentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        documentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
