package com.example.ui;

import com.example.service.UserService;
import com.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleUI(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
        logger.info("Инициализирован консольный интерфейс");
    }

    public void start() {
        logger.info("=== Консольный интерфейс запущен ===");

        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // очистка буфера

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

    private void createUser() {
        try {
            System.out.print("Введите имя: ");
            String name = scanner.nextLine();

            System.out.print("Введите email: ");
            String email = scanner.nextLine();

            System.out.print("Введите возраст: ");
            Integer age = Integer.parseInt(scanner.nextLine());

            logger.debug("Введённые данные для создания пользователя: Name='{}', Email='{}', Age={}",
                    name, email, age);

            User user = userService.createUser(name, email, age);
            System.out.println("✅ Пользователь успешно создан с ID: " + user.getId());
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат возраста при создании пользователя");
            System.out.println("Ошибка: Возраст должен быть числом!");
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка ввода данных при создании пользователя: {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при создании пользователя: ", e);
            System.out.println("Произошла непредвиденная ошибка при создании пользователя.");
        }
    }

    /**
     * Поиск пользователя по ID через консольный ввод
     */
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
        } catch (IllegalArgumentException e) {
            // Обработка случая, когда пользователь не найден (выбрасывается из getUserById)
            logger.warn("Пользователь с ID {} не найден: {}", id, e.getMessage());
            System.out.println("❌ Пользователь с ID " + id + " не найден.");
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по ID: ", e);
            System.out.println("Произошла ошибка при поиске пользователя.");
        }
    }

    /**
     * Вывод списка всех пользователей
     */
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

    /**
     * Обновление информации о пользователе через консольный ввод
     */
    private void updateUser() {
        try {
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = Long.parseLong(scanner.nextLine());

            System.out.print("Введите новое имя (или оставьте пустым): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = null;

            System.out.print("Введите новый email (или оставьте пустым): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = null;

            System.out.print("Введите новый возраст (или оставьте пустым): ");
            String ageInput = scanner.nextLine().trim();
            Integer age = ageInput.isEmpty() ? null : Integer.parseInt(ageInput);

            logger.debug("Данные для обновления пользователя ID={}: Name='{}', Email='{}', Age={}",
                    id, name, email, age);

            User updatedUser = userService.updateUser(id, name, email, age);
            System.out.println("✅ Пользователь с ID " + updatedUser.getId() + " успешно обновлён.");
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат данных при обновлении пользователя");
            System.out.println("Ошибка: Проверьте корректность введённых данных!");
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка ввода данных при обновлении пользователя: {}", e.getMessage());
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя: ", e);
            System.out.println("Произошла ошибка при обновлении пользователя.");
        }
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
