package br.tec.dev2b.app.mensagem.dto;

import br.tec.dev2b.app.mensagem.model.ConfiguracaoMensagem;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfiguracaoMensagemDto {
    private UUID id;
    private UUID usuarioId;
    private String numeroWhatsapp;
    private Boolean permitirProfissionais;
    private Boolean envioSmsAutomatico;
    private Boolean enviarComRiscoFalta;
    private String horarioDisparo;
    private String mensagemLembrete;

    public static ConfiguracaoMensagemDto from(ConfiguracaoMensagem c) {
        ConfiguracaoMensagemDto dto = new ConfiguracaoMensagemDto();
        dto.id = c.getId();
        dto.usuarioId = c.getUsuario() != null ? c.getUsuario().getId() : null;
        dto.numeroWhatsapp = c.getNumeroWhatsapp();
        dto.permitirProfissionais = c.getPermitirProfissionais();
        dto.envioSmsAutomatico = c.getEnvioSmsAutomatico();
        dto.enviarComRiscoFalta = c.getEnviarComRiscoFalta();
        dto.horarioDisparo = c.getHorarioDisparo();
        dto.mensagemLembrete = c.getMensagemLembrete();
        return dto;
    }
}
