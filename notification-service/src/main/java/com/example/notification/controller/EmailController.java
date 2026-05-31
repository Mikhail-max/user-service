package com.example.notification.controller;

import com.example.notification.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailNotificationService emailService;

    @Autowired
    public EmailController(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestParam String email, @RequestParam String operation) {
        emailService.sendNotification(email, operation);
        return ResponseEntity.ok("Письмо отправлено");
    }
}
