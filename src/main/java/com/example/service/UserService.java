package com.example.service;

import com.example.dao.UserDAO;
import com.example.model.User;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public User createUser(String name, String email, Integer age) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            user.setCreatedAt(java.time.LocalDateTime.now());
            userDAO.saveUser(user);
            return user;
        } catch (Exception e) {
            System.err.println("Ошибка при создании пользователя: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public User getUserById(Long id) {
        return userDAO.getUserById(id);
    }


    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }


    public User updateUser(Long id, String name, String email, Integer age) {
        User user = userDAO.getUserById(id);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            userDAO.updateUser(user);
        }
        return user;
    }


    public boolean deleteUser(Long id) {
        try {
            User user = userDAO.getUserById(id);
            if (user != null) {
                userDAO.deleteUser(id);
                return true;
            }
            System.out.println("Пользователь с ID " + id + " не найден.");
            return false;
        } catch (Exception e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
