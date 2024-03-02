package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class EmailServiceTest {

    private EmailService emailService;

    private JavaMailSender mailSender;

    private User user;

    @BeforeEach
    void setup() {
        mailSender = Mockito.mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
        user = new User();
        user.setUsername("BugWarsUser");
        user.setEmail("example@gmail.com");
        user.setEmailVerificationToken("49370bea-5a8c-4fba-8887-980e1b320b14");
    }

    @Test
    void sendVerificationLink_shouldSendEmail() {
        SimpleMailMessage mailMessage = emailService.sendVerificationLink(user);
        Mockito.verify(mailSender).send((SimpleMailMessage) any());
        assertEquals("example@gmail.com", mailMessage.getTo()[0]);
        assertEquals("Bug Wars: Verify Your Email", mailMessage.getSubject());
        assertTrue(mailMessage.getText().contains(user.getUsername()));
        assertTrue(mailMessage.getText().contains("/bug-wars-client#/email-verification/BugWarsUser/49370bea-5a8c-4fba-8887-980e1b320b14"));
    }
}