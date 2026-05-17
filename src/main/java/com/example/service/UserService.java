package com.example.service;

import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        logger.info("Инициализирован UserService с DAO: {}", userDAO.getClass().getSimpleName());
    }

    public User createUser(String name, String email, Integer age) {
        logger.info("Начало создания пользователя: Name='{}', Email='{}', Age={}", name, email, age);


        Validation.validateName(name);
        Validation.validateEmail(email);
        Validation.validateAge(age);

        try {
            User user = new User(name.trim(), email.toLowerCase(), age);
            userDAO.saveUser(user);
            logger.info("Пользователь успешно создан с ID: {}", user.getId());
            return user;
        } catch (Exception e) {
            logger.error("Критическая ошибка при создании пользователя (Name='{}', Email='{}'): ", name, email, e);
            throw e;
        }
    }

    public User getUserById(Long id) {
        logger.debug("Запрос пользователя по ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Попытка получения пользователя с некорректным ID: {}", id);
            throw new IllegalArgumentException("ID пользователя не может быть null или отрицательным");
        }

        try {
            User user = userDAO.getUserById(id);
            if (user != null) {
                logger.debug("Найден пользователь: ID={}, Name='{}', Email='{}'",
                        user.getId(), user.getName(), user.getEmail());
                return user;
            } else {
                logger.warn("Пользователь с ID={} не найден в базе данных", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден");
            }
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя с ID={}: ", id, e);
            throw e;
        }
    }

    public List<User> getAllUsers() {
        logger.info("Запрос списка всех пользователей");

        try {
            List<User> users = userDAO.getAllUsers();
            logger.info("Получено {} пользователей из базы данных", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей: ", e);
            throw e;
        }
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        logger.info("Начало обновления пользователя с ID={}: Name='{}', Email='{}', Age={}",
                id, name, email, age);

        User existingUser = getUserById(id);


        Validation.validateName(name);
        existingUser.setName(name.trim());

        if (email != null) {
            Validation.validateEmail(email);
            existingUser.setEmail(email.toLowerCase());
        } else {
            logger.debug("Email не обновлён (получен null), сохраняется текущее значение: '{}'", existingUser.getEmail());
        }

        if (age != null) {
            Validation.validateAge(age);
            existingUser.setAge(age);
        } else {
            logger.debug("Возраст не обновлён (получен null), сохраняется текущее значение: {}", existingUser.getAge());
        }

        try {
            userDAO.updateUser(existingUser);
            logger.info("Пользователь с ID={} успешно обновлён", id);
            return existingUser;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя с ID={}: ", id, e);
            throw e;
        }
    }

    public boolean deleteUser(Long id) {
        logger.warn("Начало удаления пользователя с ID: {}", id);

        if (id == null || id <= 0) {
            logger.warn("Попытка удаления пользователя с некорректным ID: {}", id);
            return false;
        }

        try {
            boolean isDeleted = userDAO.deleteUser(id);
            if (isDeleted) {
                logger.info("Пользователь с ID={} успешно удалён из базы данных", id);
            } else {
                logger.warn("Пользователь с ID={} не найден при попытке удаления", id);
            }
            return isDeleted;
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с ID={}: ", id, e);
            throw e;
        }
    }
}
