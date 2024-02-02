package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public SimpleMailMessage sendVerificationLink(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Bug Wars: Verify Your Email");
        String htmlContent = "Welcome! \n" +
                "Click http://localhost:5173/bug-wars-client#/email-verification/" + user.getUsername() + "/" + user.getEmailVerificationToken() +  " to verify your email.";
        mailMessage.setText(htmlContent);
        mailSender.send(mailMessage);
        return mailMessage;
    }
}
