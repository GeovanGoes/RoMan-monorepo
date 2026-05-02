package com.roman.infrastructure.email;

import com.roman.application.port.EmailPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class JavaMailEmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;

    public JavaMailEmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEmailRecuperacaoSenha(String destinatario, String linkRecuperacao) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject("RoMan — Recuperação de senha");
            helper.setText(
                "<html><body>" +
                "<h2>Recuperação de senha</h2>" +
                "<p>Clique no link abaixo para redefinir sua senha. O link expira em 1 hora.</p>" +
                "<p><a href=\"" + linkRecuperacao + "\">Redefinir senha</a></p>" +
                "<p>Se você não solicitou a recuperação, ignore este e-mail.</p>" +
                "</body></html>",
                true
            );
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao enviar e-mail de recuperação", e);
        }
    }
}
