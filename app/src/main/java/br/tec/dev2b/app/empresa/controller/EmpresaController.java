package br.tec.dev2b.app.empresa.controller;

import br.tec.dev2b.app.empresa.dto.AtualizarEmpresaDto;
import br.tec.dev2b.app.empresa.dto.CriarEmpresaDto;
import br.tec.dev2b.app.empresa.dto.EmpresaDto;
import br.tec.dev2b.app.empresa.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaDto> criar(@RequestBody CriarEmpresaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaDto>> listar() {
        return ResponseEntity.ok(empresaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmpresaDto> atualizar(@PathVariable UUID id, @RequestBody AtualizarEmpresaDto dto) {
        return ResponseEntity.ok(empresaService.atualizar(id, dto));
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadLogo(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        String url = empresaService.uploadLogo(id, file);
        return ResponseEntity.ok(Map.of("logoUrl", url));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        empresaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
