package com.example.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validation {
    public static final Logger logger = LoggerFactory.getLogger(Validation.class);


    public static void validateName(String name) {
        if (name == null) {
            logger.error("Ошибка валидации имени: имя не может быть null");
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (name.trim().isEmpty()) {
            logger.error("Ошибка валидации имени: имя не может быть пустым после обрезки пробелов");
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
    }


    public static void validateEmail(String email) {
        if (email == null) {
            logger.error("Ошибка валидации email: email не может быть null");
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            logger.error("Ошибка валидации email: некорректный формат: '{}'", email);
            throw new IllegalArgumentException("Некорректный формат email");
        }
    }

    public static void validateAge(Integer age) {
        if (age == null) {
            logger.error("Ошибка валидации возраста: возраст не может быть null");
            throw new IllegalArgumentException("Возраст не может быть пустым");
        }
        if (age < 0 || age > 150) {
            logger.error("Ошибка валидации возраста: некорректное значение: {}", age);
            throw new IllegalArgumentException("Возраст должен быть в диапазоне 0–150");
        }
    }
}
