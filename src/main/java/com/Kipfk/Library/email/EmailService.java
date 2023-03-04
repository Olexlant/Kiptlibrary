package com.Kipfk.Library.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Async
    public void sendregistrationmail(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Підтердження email");
            helper.setFrom("Library@Kipfk.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Не вдалось відправити на email", e);
            throw new IllegalStateException("Не вдалось відправити на email");
        }
    }

    @Async
    public void sendchangepasswordmail(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Відновлення паролю");
            helper.setFrom("Library@Kipfk.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Не вдалось відправити на email", e);
            throw new IllegalStateException("Не вдалось відправити на email");
        }
    }

    @Async
    public void sendNotificationMessage(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Повернення книги");
            helper.setFrom("Library@Kipfk.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Не вдалось відправити на email", e);
            throw new IllegalStateException("Не вдалось відправити на email");
        }
    }
}