package com.example.rutinafit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Value("${app.url-front}")
    private String urlFront;

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("RutinaFit <rutinafit.app@gmail.com>");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el email", e);
        }
    }

    public void enviarRecuperarPassword(String email, String token) {
        String url = urlFront + "/recuperar-password/" + token;
        String subject = "Recuperar contraseña";
        String body = """
                <html>
                <body>
                    <p>Hola,</p>
                    <p>Has solicitado recuperar tu contraseña. Haz clic en el siguiente enlace:</p>
                    <a href="%s">Recuperar contraseña</a>
                    <p>Este enlace expirará en 30 minutos.</p>
                    <p>Si no has solicitado recuperar tu contraseña, ignora este mensaje.</p>
                </body>
                </html>
                """.formatted(url);

        sendEmail(email, subject, body);
    }
}
