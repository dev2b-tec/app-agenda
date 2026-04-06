package br.tec.dev2b.app.certificado.controller;

import br.tec.dev2b.app.certificado.dto.CertificadoDigitalDto;
import br.tec.dev2b.app.certificado.service.CertificadoDigitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/certificado-digital")
@RequiredArgsConstructor
public class CertificadoDigitalController {

    private final CertificadoDigitalService certificadoService;

    @PostMapping("/upload")
    public ResponseEntity<CertificadoDigitalDto> upload(
            @RequestParam("empresaId") UUID empresaId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("senha") String senha) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificadoService.upload(empresaId, file, senha));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<CertificadoDigitalDto> buscarAtivoPorEmpresa(@PathVariable UUID empresaId) {
        return certificadoService.buscarAtivoPorEmpresa(empresaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        certificadoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
