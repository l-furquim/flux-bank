package com.fluxbank.notification_service.infrastructure.mail;

import com.fluxbank.notification_service.domain.exceptions.SendMailFailedException;
import com.fluxbank.notification_service.domain.service.MailService;
import com.fluxbank.notification_service.interfaces.dto.TransactionNotificationEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Component
public class JavaMailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String MOCK_NAME = "João Silva";
    private static final String MOCK_CPF = "12345678901";

    public JavaMailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPixReceived(TransactionNotificationEvent event) {
        try {
            String subject = "PIX Recebido -  " + event.currency() + " " + formatCurrencySimple(event.amount());
            String htmlContent = buildPixReceivedTemplate(event);

            sendEmail(event.payeeEmail(), subject, htmlContent);
            log.info("PIX received notification sent for transaction: {}", event.transactionId());
        } catch (Exception e) {
            log.error("Failed to send PIX received notification for transaction: {}", event.transactionId(), e);
            throw new SendMailFailedException("Failed to send PIX received notification " + e.getMessage());
        }
    }

    @Override
    public void sendPixSent(TransactionNotificationEvent event) {
        try {
            String subject = "PIX Enviado - " + event.currency() + " " + formatCurrencySimple(event.amount());
            String htmlContent = buildPixSentTemplate(event);

            sendEmail(event.payerEmail(), subject, htmlContent);
            log.info("PIX sent notification sent for transaction: {}", event.transactionId());
        } catch (Exception e) {
            log.error("Failed to send PIX sent notification for transaction: {}", event.transactionId(), e);
            throw new SendMailFailedException("Failed to send PIX sent notification" + e.getMessage());
        }
    }

    @Override
    public void sendPixKeyCreated() {
        try {
            log.info("PIX key created notification would be sent");
        } catch (Exception e) {
            log.error("Failed to send PIX key created notification", e);
            throw new SendMailFailedException("Failed to send PIX key created notification" + e.getMessage());
        }
    }

    @Override
    public void sendLimitExceeded() {
        try {
            log.info("Limit exceeded notification would be sent");
        } catch (Exception e) {
            log.error("Failed to send limit exceeded notification", e);
            throw new SendMailFailedException("Failed to send limit exceeded notification " + e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildPixReceivedTemplate(TransactionNotificationEvent event) {
        String template = loadTemplate("pix_received_template.html");

        return template
                .replace("{{valor}}", formatCurrency(event.amount()))
                .replace("{{nomeRemetente}}", event.payerName())
                .replace("{{cpfRemetente}}", event.payerCpf())
                .replace("{{dataHora}}", formatDateTime(event.processedAt()))
                .replace("{{idTransacao}}", event.transactionId().toString())
                .replace("{{descricao}}", event.description() != null ? event.description() : "Transferência PIX");
    }

    private String buildPixSentTemplate(TransactionNotificationEvent event) {
        String template = loadTemplate("pix_sent_template.html");

        return template
                .replace("{{valor}}", formatCurrency(event.amount()))
                .replace("{{nomeDestinatario}}", event.payeeName())
                .replace("{{cpfDestinatario}}", event.payeeCpf())
                .replace("{{chavePix}}", maskPixKey(event.account()))
                .replace("{{dataHora}}", formatDateTime(event.processedAt()))
                .replace("{{idTransacao}}", event.transactionId().toString())
                .replace("{{descricao}}", event.description() != null ? event.description() : "Transferência PIX");
    }

    private String loadTemplate(String templateName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + templateName)){

            if (inputStream == null) {
                throw new IllegalArgumentException("Template not found: " + templateName);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading the template: {}", templateName, e);
            throw new RuntimeException("Error while loading the template", e);
        }
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "R$ 0,00";

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(value);
    }

    private String formatCurrencySimple(BigDecimal value) {
        if (value == null) return "0,00";

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(value).replace("R$", "").trim();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        return dateTime.format(formatter);
    }

    private String maskDocument(String document) {
        if (document == null || document.length() < 4) return "***";

        if (document.length() == 11) {
            return document.substring(0, 3) + ".***.**" + document.substring(9);
        } else if (document.length() == 14) {
            return document.substring(0, 2) + ".***.***/****-" + document.substring(12);
        }

        return "***" + document.substring(document.length() - 2);
    }

    private String maskPixKey(String pixKey) {
        if (pixKey == null) return "***";

        if (pixKey.contains("@")) {
            String[] parts = pixKey.split("@");
            return parts[0].substring(0, Math.min(3, parts[0].length())) + "***@" + parts[1];
        } else if (pixKey.matches("\\d{11}")) {
            return maskDocument(pixKey);
        } else if (pixKey.matches("\\d{10,11}")) {
            return "(" + pixKey.substring(0, 2) + ") ****-" + pixKey.substring(pixKey.length() - 4);
        }

        return pixKey.substring(0, Math.min(4, pixKey.length())) + "***";
    }

    private String getMockUserEmail(java.util.UUID userId) {
        return "user" + userId.toString().substring(0, 8) + "@fluxbank.com";
    }
}