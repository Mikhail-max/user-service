package com.example.notification.service;

import com.example.common.dto.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    @Autowired
    private EmailNotificationService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(UserEvent event) {
        switch (event.getOperation()) {
            case "CREATE":
                emailService.sendNotification(event.getEmail(), "CREATE");
                break;
            case "DELETE":
                emailService.sendNotification(event.getEmail(), "DELETE");
                break;
        }
    }
}
