package br.tec.dev2b.app.linhadotempo.controller;

import br.tec.dev2b.app.linhadotempo.dto.LinhaDoTempoDto;
import br.tec.dev2b.app.linhadotempo.service.LinhaDoTempoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/linha-do-tempo")
@RequiredArgsConstructor
public class LinhaDoTempoController {

    private final LinhaDoTempoService linhaDoTempoService;

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<LinhaDoTempoDto>> listarPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(linhaDoTempoService.listarPorPaciente(pacienteId));
    }
}
