package com.example.service;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.example.validation.Validation.logger;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(@Valid UserCreateDto createDto) {
        User user = new User();
        user.setName(createDto.getName());
        user.setEmail(createDto.getEmail());
        user.setAge(createDto.getAge());
        User savedUser = userRepository.save(user);
        logger.info("Создан новый пользователь с ID: {}", savedUser.getId());
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
        userRepository.deleteById(id);
        logger.info("Пользователь с ID {} успешно удалён", id);
        return true;
    }

}
