package com.example.dao;

import com.example.model.User;
import com.example.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

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

    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final Integer TEST_AGE = 25;

    @BeforeEach
    void setUp() {
        // Создаём SessionFactory с параметрами из Testcontainers
        sessionFactory = HibernateUtil.buildSessionFactoryForTest(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        userDAO = new UserDAOImpl();
    }

    @AfterEach
    void tearDown() {
        // Очищаем таблицу после каждого теста
        clearUsersTable();
    }

    private void clearUsersTable() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.createNativeQuery("DELETE FROM users").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testSaveUser_Success() {// Given: создаём пользователя
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);

        userDAO.saveUser(user);

        assertNotNull(user.getId(), "ID должен быть сгенерирован после сохранения");
        assertTrue(user.getId() > 0, "ID должен быть положительным числом");

        User retrievedUser = userDAO.getUserById(user.getId());
        assertNotNull(retrievedUser, "Пользователь должен быть найден по ID после сохранения");
        assertEquals(TEST_NAME, retrievedUser.getName(), "Имя должно совпадать");
        assertEquals(TEST_EMAIL, retrievedUser.getEmail(), "Email должен совпадать");
        assertEquals(TEST_AGE, retrievedUser.getAge(), "Возраст должен совпадать");
        assertNotNull(retrievedUser.getCreatedAt(), "Дата создания должна быть установлена");
    }

    @Test
    void testGetUserById_ExistingUser() {
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        userDAO.saveUser(user);

        User retrievedUser = userDAO.getUserById(user.getId());

        assertNotNull(retrievedUser, "Пользователь должен быть найден");
        assertEquals(user.getId(), retrievedUser.getId(), "ID должен совпадать");
        assertEquals(TEST_NAME, retrievedUser.getName(), "Имя должно совпадать");
        assertEquals(TEST_EMAIL, retrievedUser.getEmail(), "Email должен совпадать");
    }

    @Test
    void testGetUserById_NonExistingUser() {

        User user = userDAO.getUserById(999L);

        assertNull(user, "Метод должен вернуть null для несуществующего ID");
    }

    @Test
    void testGetAllUsers_WithUsersInDatabase() {
        User user1 = new User("User1", "user1@example.com", 30);
        User user2 = new User("User2", "user2@example.com", 25);
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
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
        userDAO.saveUser(user);

        user.setName("Updated Name");
        user.setAge(30);
        userDAO.updateUser(user);


        User updatedUser = userDAO.getUserById(user.getId());
        assertEquals("Updated Name", updatedUser.getName(), "Имя должно быть обновлено");
        assertEquals(30, updatedUser.getAge(), "Возраст должен быть обновлён");
        assertEquals(TEST_EMAIL, updatedUser.getEmail(), "Email не должен измениться");
    }

    @Test
    void testDeleteUser_ExistingUser() {

        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
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

        User invalidUser = new User(null, TEST_EMAIL, TEST_AGE);

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

        User invalidUser = new User(TEST_NAME, "invalid-email", TEST_AGE);

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
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);

        userDAO.saveUser(user);

        assertNotNull(user.getCreatedAt(), "Поле createdAt должно быть установлено при сохранении");
        assertTrue(
                user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "Дата создания должна быть в прошлом или настоящем"
        );
    }

    @Test
    void testSaveUser_DuplicateEmail_ThrowsException() {
        User user1 = new User("User1", TEST_EMAIL, 30);
        userDAO.saveUser(user1);

        User user2 = new User("User2", TEST_EMAIL, 25);

        assertThrows(
                org.hibernate.exception.ConstraintViolationException.class,
                () -> userDAO.saveUser(user2),
                "Ожидалось исключение из‑за нарушения UNIQUE constraint на email"
        );
    }

    @Test
    void testUpdateUser_NullFields_PreservesExistingValues() {
        User user = new User(TEST_NAME, TEST_EMAIL, TEST_AGE);
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

}
