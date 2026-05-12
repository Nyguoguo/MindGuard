package com.mindguard.notification.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailMcpTool {

    private final JavaMailSender mailSender;

    public EmailMcpTool(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String subject, String body, String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(subject);
            message.setText(body);
            message.setTo(to);
            mailSender.send(message);
            log.info("Email sent to {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
