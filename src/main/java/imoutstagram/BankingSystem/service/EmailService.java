package imoutstagram.BankingSystem.service;

import imoutstagram.BankingSystem.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailDetails emailDetails) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(emailDetails.getRecipient());
            message.setSubject(emailDetails.getSubject());
            message.setText(emailDetails.getBody());

            mailSender.send(message);
            System.out.println("Email sent");

        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

}
