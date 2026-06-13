package com.example.service;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import java.util.List;

import static com.example.validation.Validation.logger;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final KafkaUserEventService kafkaUserEventService;
    private final CircuitBreaker circuitBreaker;

    public UserService(UserRepository userRepository,
                       KafkaUserEventService kafkaUserEventService,
                       CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreaker = circuitBreakerFactory.create("kafkaService");
        this.userRepository = userRepository;
        this.kafkaUserEventService = kafkaUserEventService;
    }

    @Transactional
    public User createUser(@Valid UserCreateDto createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setAge(createDto.getAge());
        User savedUser = userRepository.save(user);
        logger.info("Создан новый пользователь с ID: {}", savedUser.getId());

        circuitBreaker.run(
                () -> {
                    kafkaUserEventService.sendUserCreatedEvent(savedUser.getEmail());
                    return savedUser;
                },
                throwable -> createUserFallback(createDto, throwable)
        );

        return savedUser;
    }

    private User createUserFallback(UserCreateDto createDto, Throwable throwable) {
        logger.warn("Circuit Breaker открыт. Отправка события создания пользователя в Kafka не удалась: {}",
                throwable.getMessage());
        logger.info("Пользователь создан без отправки события в Kafka");

        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setAge(createDto.getAge());

        User savedUser = userRepository.save(user);
        logger.info("Создан пользователь с ID: {} (без отправки события в Kafka)", savedUser.getId());

        return savedUser;
    }


    @Transactional
    public User updateUser(Long id, @Valid UserUpdateDto updateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));

        if (updateDto.getName() != null) {
            existingUser.setName(updateDto.getName());
        }
        if (updateDto.getEmail() != null) {
            existingUser.setEmail(updateDto.getEmail());
        }
        if (updateDto.getAge() != null) {
            existingUser.setAge(updateDto.getAge());
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("Обновлён пользователь с ID: {}", id);
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            return false;
        }

        String email = userToDelete.getEmail();
        userRepository.deleteById(id);
        logger.info("Пользователь с ID {} успешно удалён", id);


        circuitBreaker.run(
                () -> {
                    kafkaUserEventService.sendUserDeletedEvent(email);
                    return true;
                },
                throwable -> deleteUserFallback(id, email, throwable)
        );

        return true;
    }


    private boolean deleteUserFallback(Long id, String email, Throwable throwable) {
        logger.warn("Circuit Breaker открыт. Отправка события удаления пользователя в Kafka не удалась для пользователя с ID {}: {}",
                id, throwable.getMessage());
        logger.info("Пользователь удалён без отправки события в Kafka");
        return true;
    }

}
