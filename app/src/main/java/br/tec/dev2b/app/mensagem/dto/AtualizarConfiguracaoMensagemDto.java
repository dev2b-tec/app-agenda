package br.tec.dev2b.app.mensagem.dto;

import lombok.Data;

@Data
public class AtualizarConfiguracaoMensagemDto {
    private String numeroWhatsapp;
    private Boolean permitirProfissionais;
    private Boolean envioSmsAutomatico;
    private Boolean enviarComRiscoFalta;
    private String horarioDisparo;
    private String mensagemLembrete;
}
