package br.tec.dev2b.app.evolucao.controller;

import br.tec.dev2b.app.ia.dto.RelatoAtendimentoDto;
import br.tec.dev2b.app.ia.service.IAIntegrationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evolucoes/ia")
@RequiredArgsConstructor
public class IAEvolucaoController {

    private final IAIntegrationService iaIntegrationService;

    @PostMapping("/gerar-resumo")
    public ResponseEntity<Map<String, String>> gerarResumo(@RequestBody GerarResumoRequest request) {
        String resumo = iaIntegrationService.gerarResumoEvolucao(
            request.getComentariosGerais(),
            request.getConduta(),
            request.getExamesRealizados(),
            request.getPrescricao(),
            request.getEmpresaId(),
            request.getUsuarioId()
        );

        if (resumo != null) {
            return ResponseEntity.ok(Map.of("resumo", resumo));
        }

        return ResponseEntity.ok(Map.of("resumo", "Não foi possível gerar o resumo. Verifique se há conteúdo suficiente."));
    }

    @PostMapping("/relato")
    public ResponseEntity<Map<String, String>> processarRelato(@RequestBody RelatoAtendimentoDto dto) {
        String resultado = iaIntegrationService.processarRelato(
            dto.getTranscricaoAudio(),
            dto.getEspecialidade(),
            dto.getEmpresaId(),
            dto.getUsuarioId()
        );

        if (resultado != null) {
            return ResponseEntity.ok(Map.of("texto", resultado));
        }

        return ResponseEntity.internalServerError()
            .body(Map.of("erro", "Não foi possível processar o relato"));
    }

    @PostMapping("/atendimento")
    public ResponseEntity<Map<String, String>> processarAtendimento(@RequestBody RelatoAtendimentoDto dto) {
        String resultado = iaIntegrationService.processarAtendimento(
            dto.getTranscricaoAudio(),
            dto.getEspecialidade(),
            dto.getEmpresaId(),
            dto.getUsuarioId()
        );

        if (resultado != null) {
            return ResponseEntity.ok(Map.of("texto", resultado));
        }

        return ResponseEntity.internalServerError()
            .body(Map.of("erro", "Não foi possível processar o atendimento"));
    }

    @PostMapping("/sugestao")
    public ResponseEntity<Map<String, String>> gerarSugestao(@RequestBody SugestaoRequest request) {
        String resultado = iaIntegrationService.gerarSugestaoComentarios(
            request.getTextoAtual(),
            request.getEspecialidade(),
            request.getEmpresaId(),
            request.getUsuarioId()
        );

        if (resultado != null) {
            return ResponseEntity.ok(Map.of("sugestao", resultado));
        }

        return ResponseEntity.internalServerError()
            .body(Map.of("erro", "Não foi possível gerar a sugestão"));
    }

    @PostMapping(value = "/processar-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> processarAudio(
            @RequestPart("audio") MultipartFile audio,
            @RequestParam("tipo") String tipo,
            @RequestParam("especialidade") String especialidade,
            @RequestParam("empresaId") UUID empresaId,
            @RequestParam(value = "usuarioId", required = false) UUID usuarioId) {

        // 1. Transcrever o \u00e1udio via app-integration-ia (Whisper)
        String transcricao = iaIntegrationService.transcreverAudio(audio);
        if (transcricao == null || transcricao.isBlank()) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "N\u00e3o foi poss\u00edvel transcrever o \u00e1udio"));
        }

        // 2. Processar a transcri\u00e7\u00e3o com IA de acordo com o tipo e especialidade
        String resultado;
        if ("relato".equals(tipo)) {
            resultado = iaIntegrationService.processarRelato(transcricao, especialidade, empresaId, usuarioId);
        } else {
            resultado = iaIntegrationService.processarAtendimento(transcricao, especialidade, empresaId, usuarioId);
        }

        if (resultado != null) {
            return ResponseEntity.ok(Map.of("texto", resultado, "transcricao", transcricao));
        }

        return ResponseEntity.internalServerError()
                .body(Map.of("erro", "N\u00e3o foi poss\u00edvel processar o \u00e1udio"));
    }

    @Data
    public static class GerarResumoRequest {
        private String comentariosGerais;
        private String conduta;
        private String examesRealizados;
        private String prescricao;
        private UUID empresaId;
        private UUID usuarioId;
    }

    @Data
    public static class SugestaoRequest {
        private String textoAtual;
        private String especialidade;
        private UUID empresaId;
        private UUID usuarioId;
    }
}
