package br.tec.dev2b.app.termo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AceitarTermoDto {
    private UUID termoId;
    private UUID empresaId;
    private UUID usuarioId;
}
