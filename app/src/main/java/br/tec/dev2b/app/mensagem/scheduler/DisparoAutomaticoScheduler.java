package br.tec.dev2b.app.mensagem.scheduler;

import br.tec.dev2b.app.mensagem.service.DisparoAutomaticoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Agendador do disparo automático de mensagens WhatsApp.
 *
 * <p>Executa diariamente às 08:00 (horário do servidor).
 * O horário pode ser ajustado via {@code disparo.cron} no application.properties.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DisparoAutomaticoScheduler {

    private final DisparoAutomaticoService disparoAutomaticoService;

    /**
     * Job diário de confirmação de presença.
     * Default: todo dia às 08:00. Override via disparo.confirmacao.cron.
     */
    @Scheduled(cron = "${disparo.confirmacao.cron:0 0 8 * * *}")
    public void executarConfirmacao() {
        log.info("[SCHEDULER] Executando job de confirmação automática...");
        int enviados = disparoAutomaticoService.executarDisparoConfirmacao();
        log.info("[SCHEDULER] Job de confirmação concluído — {} mensagens enviadas", enviados);
    }

    /**
     * Job diário de remarcação.
     * Default: todo dia às 09:00. Override via disparo.remarcacao.cron.
     */
    @Scheduled(cron = "${disparo.remarcacao.cron:0 0 9 * * *}")
    public void executarRemarcacao() {
        log.info("[SCHEDULER] Executando job de remarcação automática...");
        int enviados = disparoAutomaticoService.executarDisparoRemarcacao();
        log.info("[SCHEDULER] Job de remarcação concluído — {} mensagens enviadas", enviados);
    }
}
