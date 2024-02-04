package net.crusadergames.bugwars.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailConfigTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void getMailSender_shouldReturnMailSender() {

    }

    @Test
    void deleteExpiredAccounts_shouldDeleteExpiredAccounts() {

    }


}