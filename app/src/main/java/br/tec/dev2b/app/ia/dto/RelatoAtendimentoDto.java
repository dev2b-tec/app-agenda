package br.tec.dev2b.app.ia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatoAtendimentoDto {
    private String transcricaoAudio;
    private String especialidade; // MEDICO, NUTRICIONISTA, etc
    private UUID empresaId;
    private UUID usuarioId;
}
