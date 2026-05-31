package com.example.ui;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.service.UserService;
import com.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Scanner;

@Service
public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private final UserService userService;
    private final Scanner scanner;
    private final ConfigurableApplicationContext context;

    public ConsoleUI(UserService userService, ConfigurableApplicationContext context) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
        this.context = context;
        logger.info("Инициализирован консольный интерфейс");
    }

    public void start() {
        logger.info("=== Консольный интерфейс запущен ===");

        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    logger.info("Выбран пункт меню: Создание пользователя");
                    createUser();
                    break;
                case 2:
                    logger.info("Выбран пункт меню: Поиск пользователя по ID");
                    getUserById();
                    break;
                case 3:
                    logger.info("Выбран пункт меню: Список всех пользователей");
                    getAllUsers();
                    break;
                case 4:
                    logger.info("Выбран пункт меню: Обновление пользователя");
                    updateUser();
                    break;
                case 5:
                    logger.info("Выбран пункт меню: Удаление пользователя");
                    deleteUser();
                    break;
                case 0:
                    logger.info("Выход из приложения");
                    System.out.println("До свидания!");
                    scanner.close();
                    SpringApplication.exit(context);
                    return;
                default:
                    logger.warn("Некорректный выбор пункта меню: {}", choice);
                    System.out.println("Неверный пункт меню. Попробуйте снова.");
            }
        }
    }

    /**
     * Отображение главного меню
     */
    private void showMenu() {
        System.out.println("\n=== Управление пользователями ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Список всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private UserCreateDto getUserCreateInput() {
        UserCreateDto dto = new UserCreateDto();
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("❌ Имя не может быть пустым. Попробуйте снова.");
                continue;
            }
            dto.setName(name);

            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("❌ Email не может быть пустым. Попробуйте снова.");
                continue;
            } else if (!isValidEmail(email)) {
                System.out.println("❌ Email должен быть корректным. Попробуйте снова.");
                continue;
            }
            dto.setEmail(email);

            System.out.print("Введите возраст: ");
            String ageInput = scanner.nextLine().trim();
            try {
                int age = Integer.parseInt(ageInput);
                if (age < 0) {
                    System.out.println("❌ Возраст должен быть неотрицательным числом. Попробуйте снова.");
                    continue;
                }
                dto.setAge(age);
            } catch (NumberFormatException e) {
                System.out.println("❌ Возраст должен быть числом. Попробуйте снова.");
                continue;
            }

            validInput = true; // Все поля прошли проверку
        }

        return dto;
    }

    // Вспомогательный метод для проверки email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }


    private void createUser() {
        try {
            UserCreateDto createDto = getUserCreateInput();
            User createdUser = userService.createUser(createDto);
            System.out.println("✅ Пользователь успешно создан с ID: " + createdUser.getId());
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: ", e);
            System.out.println("Произошла ошибка при создании пользователя.");
        }
    }

    private void updateUser() {
        Long updateId = null;
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            updateId = Long.parseLong(scanner.nextLine());
            logger.debug("Получен ID пользователя для обновления: {}", updateId);

            UserUpdateDto updateDto = getUserUpdateInput();
            userService.updateUser(updateId, updateDto);
            System.out.println("✅ Пользователь успешно обновлён!");
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат ID при обновлении пользователя");
            System.out.println("Ошибка: ID должен быть числом!");
        } catch (UserNotFoundException e) {
            logger.warn("Пользователь с ID {} не найден: {}", updateId, e.getMessage());
            System.out.println("❌ Пользователь с ID " + updateId + " не найден.");
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя: ", e);
            System.out.println("Произошла ошибка при обновлении пользователя.");
        }
    }





    private void getUserById() {
        Long id = null;
        try {
            System.out.print("Введите ID пользователя: ");
            id = Long.parseLong(scanner.nextLine());

            logger.debug("Запрос пользователя с ID: {}", id);
            User user = userService.getUserById(id); // Теперь напрямую получаем User

            System.out.println("👤 Найден пользователь:");
            System.out.println("ID: " + user.getId());
            System.out.println("Имя: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Возраст: " + user.getAge());
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат ID при поиске пользователя");
            System.out.println("Ошибка: ID должен быть числом!");
        } catch (UserNotFoundException  e) {
            // Обработка случая, когда пользователь не найден (выбрасывается из getUserById)
            logger.warn("Пользователь с ID {} не найден: {}", id, e.getMessage());
            System.out.println("❌ Пользователь с ID " + id + " не найден.");
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: ", e);
            System.out.println("Произошла ошибка при поиске пользователя.");
        }
    }


    private void getAllUsers() {
        try {
            logger.info("Запрос списка всех пользователей через UI");
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("📭 Список пользователей пуст.");
            } else {
                System.out.println("👥 Список всех пользователей (" + users.size() + "):");
                for (User user : users) {
                    System.out.printf("ID: %d, Имя: %s, Email: %s, Возраст: %d%n",
                            user.getId(), user.getName(), user.getEmail(), user.getAge());
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей: ", e);
            System.out.println("Произошла ошибка при получении списка пользователей.");
        }
    }

    private UserUpdateDto getUserUpdateInput() {
        UserUpdateDto dto = new UserUpdateDto();

        System.out.print("Введите новое имя (оставьте пустым, чтобы не менять): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            dto.setName(name);
        }

        System.out.print("Введите новый email (оставьте пустым, чтобы не менять): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            if (!isValidEmail(email)) {
                System.out.println("❌ Email должен быть корректным. Поле не будет обновлено.");
            } else {
                dto.setEmail(email);
            }
        }

        System.out.print("Введите новый возраст (оставьте пустым, чтобы не менять): ");
        String ageInput = scanner.nextLine().trim();
        if (!ageInput.isEmpty()) {
            try {
                int age = Integer.parseInt(ageInput);
                if (age < 0) {
                    System.out.println("❌ Возраст должен быть неотрицательным числом. Поле не будет обновлено.");
                } else {
                    dto.setAge(age);
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Возраст должен быть числом. Поле не будет обновлено.");
            }
        }

        return dto;
    }



    private void deleteUser() {
        try {
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = Long.parseLong(scanner.nextLine());

            logger.warn("Попытка удаления пользователя с ID: {}", id);
            boolean isDeleted = userService.deleteUser(id);

            if (isDeleted) {
                System.out.println("✅ Пользователь с ID " + id + " успешно удалён.");
            } else {
                System.out.println("❌ Пользователь с ID " + id + " не найден или не удалён.");
            }
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат ID при удалении пользователя");
            System.out.println("Ошибка: ID должен быть числом!");
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя: ", e);
            System.out.println("Произошла ошибка при удалении пользователя.");
        }
    }
}
