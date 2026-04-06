package br.tec.dev2b.app.anamnese.controller;

import br.tec.dev2b.app.anamnese.dto.RespostaAnamneseDto;
import br.tec.dev2b.app.anamnese.dto.SalvarRespostaAnamneseDto;
import br.tec.dev2b.app.anamnese.service.RespostaAnamneseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/respostas-anamnese")
@RequiredArgsConstructor
public class RespostaAnamneseController {

    private final RespostaAnamneseService respostaAnamneseService;

    @GetMapping("/paciente/{pacienteId}/anamnese/{anamneseId}")
    public ResponseEntity<RespostaAnamneseDto> buscar(
            @PathVariable UUID pacienteId,
            @PathVariable UUID anamneseId) {
        return respostaAnamneseService.buscar(pacienteId, anamneseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<RespostaAnamneseDto> salvar(@RequestBody SalvarRespostaAnamneseDto dto) {
        return ResponseEntity.ok(respostaAnamneseService.salvar(dto));
    }
}
