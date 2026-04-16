package br.tec.dev2b.app.integracao.service;

import br.tec.dev2b.app.integracao.repository.IntegracaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SmtpService {

    private static final String TIPO = "SMTP";

    private final IntegracaoRepository integracaoRepository;

    /**
     * Constrói um JavaMailSenderImpl dinâmico com as configurações SMTP da empresa.
     * Lança IllegalStateException se não houver configuração para a empresa.
     */
    public JavaMailSenderImpl buildMailSender(UUID empresaId) {
        Map<String, String> cfg = integracaoRepository
                .findByEmpresaIdAndTipo(empresaId, TIPO)
                .map(br.tec.dev2b.app.integracao.model.Integracao::getConfiguracao)
                .orElseThrow(() -> new IllegalStateException(
                        "Configuração SMTP não encontrada para a empresa: " + empresaId));

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(cfg.getOrDefault("host", ""));
        sender.setPort(Integer.parseInt(cfg.getOrDefault("porta", "587")));
        sender.setUsername(cfg.getOrDefault("usuario", ""));
        sender.setPassword(cfg.getOrDefault("senha", ""));

        boolean usarTls = Boolean.parseBoolean(cfg.getOrDefault("usarTls", "true"));

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        if (usarTls) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        } else {
            props.put("mail.smtp.ssl.enable", "true");
        }
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");

        return sender;
    }

    /**
     * Envia um e-mail simples usando as configurações SMTP da empresa.
     */
    public void enviar(UUID empresaId, String destinatario, String assunto, String corpo) {
        JavaMailSenderImpl sender = buildMailSender(empresaId);

        Map<String, String> cfg = integracaoRepository
                .findByEmpresaIdAndTipo(empresaId, TIPO)
                .map(br.tec.dev2b.app.integracao.model.Integracao::getConfiguracao)
                .orElseThrow();

        String from = cfg.getOrDefault("emailRemetente", sender.getUsername());
        String fromName = cfg.getOrDefault("nomeRemetente", from);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(String.format("%s <%s>", fromName, from));
        msg.setTo(destinatario);
        msg.setSubject(assunto);
        msg.setText(corpo);

        sender.send(msg);
    }

    /**
     * Envia um e-mail de teste para verificar se a configuração SMTP está correta.
     */
    public void testar(UUID empresaId, String emailTeste) {
        enviar(
                empresaId,
                emailTeste,
                "Teste de configuração SMTP — DEV2B",
                "Parabéns! Seu servidor SMTP está configurado corretamente na plataforma DEV2B."
        );
    }
}
