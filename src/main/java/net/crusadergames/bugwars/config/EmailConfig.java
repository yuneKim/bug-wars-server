package net.crusadergames.bugwars.config;

import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableScheduling
public class EmailConfig {

    @Autowired
    UserRepository userRepository;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.password}")
    String password;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return mailSender;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredAccounts() {
        List<User> users = userRepository.findByIsEmailVerifiedFalse();
        for (User user : users) {
            if (LocalDateTime.now().isAfter(user.getEmailVerificationExpiry())) {
                userRepository.delete(user);
            }
        }
    }
}
