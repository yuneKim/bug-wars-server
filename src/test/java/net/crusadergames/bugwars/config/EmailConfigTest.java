package net.crusadergames.bugwars.config;

import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {"spring.mail.username=testUser", "spring.mail.password=testPassword"})
@ExtendWith(MockitoExtension.class)
class EmailConfigTest {

    @Autowired
    private EmailConfig emailConfig;

    @MockBean
    private UserRepository userRepository;

    List<User> users;

    @Test
    void getJavaMailSender_shouldNotBeNull() {
        assertNotNull(emailConfig.getJavaMailSender());
    }

    @Test
    void getJavaMailSender_shouldReturnAValidMailSender() {
        JavaMailSenderImpl actualMailSender = (JavaMailSenderImpl)emailConfig.getJavaMailSender();

        assertEquals("smtp.gmail.com", actualMailSender.getHost());
        assertEquals(465, actualMailSender.getPort());
        assertEquals("testUser", actualMailSender.getUsername());
        assertEquals("testPassword", actualMailSender.getPassword());
    }

    @Test
    void deleteExpiredAccounts_shouldDeleteExpiredAccounts() {
        User user1 = new User("user1", "user1@gmail.com", "user1Password");
        User user2 = new User("user2", "user2@gmail.com", "user2Password");
        User user3 = new User("user3", "user3@gmail.com", "user3Password");
        user1.setEmailVerificationExpiry(LocalDateTime.now().minusDays(1L));
        user2.setEmailVerificationExpiry(LocalDateTime.now().minusDays(1L));
        user3.setEmailVerificationExpiry(LocalDateTime.now().minusDays(1L));
        users = List.of(user1, user2, user3);
        Mockito.when(userRepository.findByIsEmailVerifiedFalse()).thenReturn(users);

        emailConfig.deleteExpiredAccounts();

        Mockito.verify(userRepository).delete(user1);
        Mockito.verify(userRepository).delete(user2);
        Mockito.verify(userRepository).delete(user3);
    }

}