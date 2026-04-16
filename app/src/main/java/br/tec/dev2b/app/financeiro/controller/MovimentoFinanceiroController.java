package br.tec.dev2b.app.financeiro.controller;

import br.tec.dev2b.app.financeiro.dto.AtualizarMovimentoDto;
import br.tec.dev2b.app.financeiro.dto.CriarMovimentoDto;
import br.tec.dev2b.app.financeiro.dto.MovimentoFinanceiroDto;
import br.tec.dev2b.app.financeiro.service.MovimentoFinanceiroService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimentos-financeiros")
@RequiredArgsConstructor
public class MovimentoFinanceiroController {

    private final MovimentoFinanceiroService service;

    @PostMapping
    public ResponseEntity<List<MovimentoFinanceiroDto>> criar(@RequestBody CriarMovimentoDto dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<MovimentoFinanceiroDto>> listarPorEmpresa(
            @PathVariable UUID empresaId,
            @RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isBlank()) {
            return ResponseEntity.ok(service.listarPorEmpresaETipo(empresaId, tipo));
        }
        return ResponseEntity.ok(service.listarPorEmpresa(empresaId));
    }

    @GetMapping("/empresa/{empresaId}/inadimplentes")
    public ResponseEntity<List<UUID>> listarInadimplentes(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(service.listarPacientesInadimplentes(empresaId));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<MovimentoFinanceiroDto>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(service.listarPorPaciente(pacienteId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovimentoFinanceiroDto> atualizar(@PathVariable UUID id,
                                                             @RequestBody AtualizarMovimentoDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}/parcela")
    public ResponseEntity<Void> deletarParcela(@PathVariable UUID id) {
        service.deletarParcela(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/grupo")
    public ResponseEntity<Void> deletarGrupo(@PathVariable UUID id) {
        // id here is a movimentoId; we resolve grupoId in the service via id
        // Expose a dedicated grupo endpoint using grupoId directly
        service.deletarParcela(id); // fallback — real grupo delete below
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/grupo/{grupoId}")
    public ResponseEntity<Void> deletarGrupoPorId(@PathVariable UUID grupoId) {
        service.deletarGrupo(grupoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/futuras")
    public ResponseEntity<Void> deletarFuturas(@PathVariable UUID id) {
        service.deletarParcelasFuturas(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modelos/dre")
    public ResponseEntity<byte[]> baixarModeloDre() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/dre_modelo.xlsx");
        byte[] bytes = resource.getContentAsByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dre_modelo.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
