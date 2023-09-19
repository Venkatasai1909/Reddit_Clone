package com.javateam.service;

import com.javateam.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailNotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmailNotification(User user, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("New Post Notification From Reddit");
        mailMessage.setText(message);

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
