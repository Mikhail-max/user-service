package com.example.service;

import com.example.common.dto.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaUserEventService {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserEventService.class);

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void sendUserCreatedEvent(String email) {
        UserEvent event = new UserEvent("CREATE", email);
        try {
            kafkaTemplate.send(TOPIC, event);
            log.info("Событие создания пользователя отправлено в Kafka: email={}", email);
        } catch (Exception e) {
            log.error("Не удалось отправить событие создания пользователя в Kafka (email={}). Пользователь сохранён в БД, но событие не доставлено.", email, e);
        }
    }

    public void sendUserDeletedEvent(String email) {
        UserEvent event = new UserEvent("DELETE", email);
        try {
            kafkaTemplate.send(TOPIC, event);
            log.info("Событие удаления пользователя отправлено в Kafka: email={}", email);
        } catch (Exception e) {
            log.error("Не удалось отправить событие удаления пользователя в Kafka (email={}). Операция завершена, но событие не доставлено.", email, e);
        }
    }
}
