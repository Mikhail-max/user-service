package com.example.ui;

import com.example.model.User;
import com.example.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleUI(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        System.out.println("---------Сервис управления Пользователями---------");
        boolean running = true;
        while (running){
            showMenu();
            int choice = getUserChoice();

            switch (choice){
                case 1 -> createUser();
                case 2 -> getUserById();
                case 3 -> getAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    System.out.println("Выход из приложения...");
                running = false;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
        scanner.close();
    }

    private void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());

        boolean success = userService.deleteUser(id);
        if (success) {
            System.out.println("Пользователь с ID " + id + " удалён.");
        } else {
            System.out.println("Не удалось удалить пользователя с ID " + id + ".");
        }
    }

    private void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новое имя: ");
        String name = scanner.nextLine();
        System.out.print("Новый email: ");
        String email = scanner.nextLine();
        System.out.print("Новый возраст: ");
        Integer age = Integer.parseInt(scanner.nextLine());

        var updatedUser = userService.updateUser(id, name, email, age);
        if (updatedUser != null) {
            System.out.println("Пользователь обновлён: " + updatedUser.getName());
        } else {
            System.out.println("Ошибка при обновлении пользователя.");
        }
    }

    private void getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("В базе нет пользователей.");
        } else {
            System.out.println("Список всех пользователей:");
            for (User user : users) {
                System.out.println("- " + user.getName() + " (ID: " + user.getId()  + " Email: " + user.getEmail() +
                        " age: " + user.getAge() + " Создан: " + user.getCreatedAt() + ")");
            }
        }
    }

    private void getUserById() {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        var user = userService.getUserById(id);

        if (user != null) {
            System.out.println("Найден пользователь: " + user.getName() +
                    " (ID: " + user.getId() +  " Email: " + user.getEmail() +
                    " age: " + user.getAge() + " Создан: " + user.getCreatedAt() + ")" );
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private void createUser() {
        System.out.print("Введите имя пользователя: ");
        String name = scanner.nextLine();
        System.out.print("Введите почту пользователя: ");
        String email = scanner.nextLine();
        System.out.print("Введите возраст пользователя: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = userService.createUser(name, email, age);
        if(user != null) {
                System.out.println("Пользователь создан с ID: " + user.getId());
            } else{
                System.out.println("Ошибка при создании пользователя.");
            }
        }



    private void showMenu(){
        System.out.println("/n----Меню----");
        System.out.println("1.Создать пользователя");
        System.out.println("2.Получить пользователя по айди");
        System.out.println("3.Получить всех пользователей");
        System.out.println("4.Обновить данные пользователя");
        System.out.println("5.Удалить пользователя");
        System.out.println("0.Выход");
        System.out.print("Выберите действие: ");
    }

    private int getUserChoice(){
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Пожалуйста введите число.");
            return -1;
        }
    }
    
}
