package com.example.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNotification(String email, String operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Уведомление о вашем аккаунте");

        String text = switch (operation) {
            case "CREATE" -> "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
            case "DELETE" -> "Здравствуйте! Ваш аккаунт был удалён.";
            default -> "Неизвестное событие";
        };

        message.setText(text);
        mailSender.send(message);
    }
}

