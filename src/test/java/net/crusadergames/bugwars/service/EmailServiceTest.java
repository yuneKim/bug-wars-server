package net.crusadergames.bugwars.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {
    @Test
    void sendNewMail_shouldSendEmail() {
        EmailService emailService = new EmailService();
        emailService.sendNewMail("alissa6710@gmail.com", "bugwarsofficial@gmail.com", "test email");
    }
}