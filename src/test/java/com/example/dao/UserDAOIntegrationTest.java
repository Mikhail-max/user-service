package com.example.dao;

import com.example.model.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDAOImplIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private UserDAO userDAO;
    private SessionFactory sessionFactory;

    private static final String BASE_NAME = "Test User";
    private static final Integer BASE_AGE = 25;

    @BeforeEach
    void setUp() {
        System.out.println("setUp() is running...");

        sessionFactory = HibernateUtil.buildSessionFactoryForTest(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        userDAO = new UserDAOImpl(sessionFactory);

        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * Универсальный метод очистки БД — удаляет все записи из таблицы users
     */
    private void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                int deletedCount = session.createNativeQuery("DELETE FROM users").executeUpdate();
                tx.commit();
                System.out.println("Удалено записей: " + deletedCount);

                Long remainingCount = session.createNativeQuery(
                        "SELECT COUNT(*) FROM users", Long.class).getSingleResult();
                System.out.println("Осталось записей после очистки: " + remainingCount);
            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }


    private String generateUniqueEmail() {
        return "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    @Test
    void testSaveUser_Success() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);

        userDAO.saveUser(user);

        assertNotNull(user.getId(), "ID должен быть сгенерирован после сохранения");
        assertTrue(user.getId() > 0, "ID должен быть положительным числом");

        // Перезагружаем объект из БД для корректной проверки
        User retrievedUser = userDAO.getUserById(user.getId());
        assertNotNull(retrievedUser, "Пользователь должен быть найден по ID после сохранения");
        assertEquals(BASE_NAME, retrievedUser.getName(), "Имя должно совпадать");
        assertEquals(uniqueEmail, retrievedUser.getEmail(), "Email должен совпадать");
        assertEquals(BASE_AGE, retrievedUser.getAge(), "Возраст должен совпадать");
        assertNotNull(retrievedUser.getCreatedAt(), "Дата создания должна быть установлена");
    }


    @Test
    void testGetUserById_ExistingUser() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);
        userDAO.saveUser(user);

        User retrievedUser = userDAO.getUserById(user.getId());

        assertNotNull(retrievedUser, "Пользователь должен быть найден");
        assertEquals(user.getId(), retrievedUser.getId(), "ID должен совпадать");
        assertEquals(BASE_NAME, retrievedUser.getName(), "Имя должно совпадать");
        assertEquals(uniqueEmail, retrievedUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void testGetUserById_NonExistingUser() {
        User user = userDAO.getUserById(999L);
        assertNull(user, "Метод должен вернуть null для несуществующего ID");
    }

    @Test
    void testGetAllUsers_WithUsersInDatabase() {
        String email1 = generateUniqueEmail();
        String email2 = generateUniqueEmail();

        User user1 = new User("User1", email1, 30);
        User user2 = new User("User2", email2, 25);
        userDAO.saveUser(user1);
        userDAO.saveUser(user2);

        List<User> users = userDAO.getAllUsers();

        assertEquals(2, users.size(), "Должно быть возвращено 2 пользователя");
        assertTrue(users.contains(user1), "Список должен содержать user1");
        assertTrue(users.contains(user2), "Список должен содержать user2");
    }

    @Test
    void testGetAllUsers_EmptyDatabase() {
        List<User> users = userDAO.getAllUsers();

        assertNotNull(users, "Метод не должен возвращать null");
        assertTrue(users.isEmpty(), "Список должен быть пустым при отсутствии пользователей");
    }


    @Test
    void testUpdateUser_Success() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);
        userDAO.saveUser(user);

        User updatedUser = userDAO.getUserById(user.getId());
        updatedUser.setName("Updated Name");
        updatedUser.setAge(30);
        userDAO.updateUser(updatedUser);

        User finalUser = userDAO.getUserById(user.getId());
        assertEquals("Updated Name", finalUser.getName(), "Имя должно быть обновлено");
        assertEquals(30, finalUser.getAge(), "Возраст должен быть обновлён");
        assertEquals(uniqueEmail, finalUser.getEmail(), "Email не должен измениться");
    }


    @Test
    void testDeleteUser_ExistingUser() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);
        userDAO.saveUser(user);

        boolean isDeleted = userDAO.deleteUser(user.getId());

        assertTrue(isDeleted, "Метод должен вернуть true при успешном удалении");
        assertNull(userDAO.getUserById(user.getId()), "Пользователь не должен быть найден после удаления");
    }

    @Test
    void testDeleteUser_NonExistingUser() {
        boolean isDeleted = userDAO.deleteUser(999L);
        assertFalse(isDeleted, "Метод должен вернуть false при попытке удалить несуществующего пользователя");
    }

    @Test
    void testSaveUser_ValidationFails_NullName() {
        String uniqueEmail = generateUniqueEmail();
        User invalidUser = new User(null, uniqueEmail, BASE_AGE);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userDAO.saveUser(invalidUser),
                "Ожидалось исключение валидации для null имени"
        );

        assertTrue(
                exception.getMessage().contains("Имя пользователя не может быть пустым"),
                "Сообщение исключения должно содержать корректное сообщение валидации"
        );
    }

    @Test
    void testSaveUser_ValidationFails_InvalidEmail() {
        User invalidUser = new User(BASE_NAME, "invalid-email", BASE_AGE);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userDAO.saveUser(invalidUser),
                "Ожидалось исключение валидации для некорректного email"
        );

        assertTrue(
                exception.getMessage().contains("Некорректный формат email"),
                "Сообщение исключения должно содержать корректное сообщение валидации"
        );
    }

    @Test
    void testSaveUser_CreatedAtIsSet() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);

        userDAO.saveUser(user);

        assertNotNull(user.getCreatedAt(), "Поле createdAt должно быть установлено при сохранении");
        assertTrue(
                user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "Дата создания должна быть в прошлом или настоящем"
        );
    }

    @Test
    void testSaveUser_DuplicateEmail_ThrowsException() {
        String uniqueEmail = generateUniqueEmail();

        User user1 = new User("User1", uniqueEmail, 30);
        userDAO.saveUser(user1);

        User user2 = new User("User2", uniqueEmail, 25);

        assertThrows(
                org.hibernate.exception.ConstraintViolationException.class,
                () -> userDAO.saveUser(user2),
                "Ожидалось исключение из-за нарушения UNIQUE constraint на email"
        );
    }

    @Test
    void testUpdateUser_NullFields_PreservesExistingValues() {
        String uniqueEmail = generateUniqueEmail();
        User user = new User(BASE_NAME, uniqueEmail, BASE_AGE);
        userDAO.saveUser(user);

        user.setName(null);
        user.setAge(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userDAO.updateUser(user),
                "Ожидалось исключение валидации при null в имени или возрасте"
        );

        assertTrue(
                exception.getMessage().contains("Имя пользователя не может быть пустым") ||
                        exception.getMessage().contains("Возраст не может быть пустым"),
                "Сообщение исключения должно указывать на поле с ошибкой"
        );
    }

    @Test
    void testConcurrentSave() {
        String email1 = generateUniqueEmail();
        String email2 = generateUniqueEmail();

        User user1 = new User("User1", email1, 30);
        User user2 = new User("User2", email2, 25);

        userDAO.saveUser(user1);
        userDAO.saveUser(user2);

        List<User> users = userDAO.getAllUsers();
        System.out.println("Всего пользователей в БД: " + users.size());
        users.forEach(u -> System.out.println("User: " + u.getId() + ", " + u.getName() + ", " + u.getEmail()));

        assertEquals(2, users.size(), "Должно быть 2 пользователя: user1 и user2");
    }



    @Test
    void testEmailUniqueness() {
        String uniqueEmail = generateUniqueEmail();

        User user1 = new User("UserA", uniqueEmail, 25);
        userDAO.saveUser(user1);

        User user2 = new User("UserB", uniqueEmail, 30);

        assertThrows(
                org.hibernate.exception.ConstraintViolationException.class,
                () -> userDAO.saveUser(user2),
                "Должен сработать уникальный индекс на email"
        );
    }
}

