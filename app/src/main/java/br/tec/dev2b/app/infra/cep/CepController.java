package br.tec.dev2b.app.infra.cep;

import br.tec.dev2b.app.infra.cep.ViaCepClient.ViaCepDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cep")
@RequiredArgsConstructor
public class CepController {

    private final CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<ViaCepDto> consultar(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.consultar(cep));
    }
}
