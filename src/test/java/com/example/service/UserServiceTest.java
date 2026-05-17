package com.example.service;

import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;


    @InjectMocks
    private UserService userService;

    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final Integer TEST_AGE = 25;
    private static final long TEST_USER_ID = 1L;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_NAME.trim(), TEST_EMAIL.toLowerCase(), TEST_AGE);
        testUser.setId(TEST_USER_ID);
    }

    @Test
    void testCreateUser_Success() {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(TEST_USER_ID); // Имитируем установку ID от БД
            return null;
        }).when(userDAO).saveUser(any(User.class));


        User result = userService.createUser(TEST_NAME, TEST_EMAIL, TEST_AGE);


        assertNotNull(result);
        assertEquals(TEST_NAME.trim(), result.getName());
        assertEquals(TEST_EMAIL.toLowerCase(), result.getEmail());
        assertEquals(TEST_AGE, result.getAge());
        assertEquals(TEST_USER_ID, result.getId());


        verify(userDAO, times(1)).saveUser(argThat(user ->
                TEST_NAME.trim().equals(user.getName()) &&
                        TEST_EMAIL.toLowerCase().equals(user.getEmail()) &&
                        TEST_AGE.equals(user.getAge())
        ));
    }

    @Test
    void testCreateUser_NameNull_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(null, TEST_EMAIL, TEST_AGE),
                "Ожидалось исключение при передаче null в качестве имени");
        assertTrue(
                exception.getMessage().toLowerCase().contains("имя"),
                "Сообщение исключения должно содержать упоминание поля 'имя'"
        );
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_EmptyName_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser("", TEST_EMAIL, TEST_AGE),
                "Ожидалось исключение при передаче null в качестве имени");
        assertTrue(
                exception.getMessage().toLowerCase().contains("имя"),
                "Сообщение исключения должно содержать упоминание поля 'имя'"
        );
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_InvalidEmail_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(TEST_NAME, "invalid-email", TEST_AGE),
                "Ожидается исключение при передаче invalid-email в качестве почты");
        assertTrue(
                exception.getMessage().toLowerCase().contains("email"),
                "Сообщение исключения должно содержать упоминание 'email'"
        );
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_NegativeAge_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(TEST_NAME, TEST_EMAIL, -5),
                "Ожидалось исключение при передаче отриц. возраста");
        assertTrue(
                exception.getMessage().toLowerCase().contains("age") ||
                exception.getMessage().toLowerCase().contains("возраст"),
                "Сообщение исключения должно содержать упоминание 'age' или 'возраст'"
        );
        verify(userDAO, never()).saveUser(any(User.class));
    }
    @Test
    void testCreateUser_AgeNull_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        userService.createUser(TEST_NAME, TEST_EMAIL, null),
                "Ожидалось исключение при передаче null возраста");
        assertTrue(
                exception.getMessage().toLowerCase().contains("age") ||
                        exception.getMessage().toLowerCase().contains("возраст")||
                        exception.getMessage().toLowerCase().contains("null"),
                "Сообщение исключения должно содержать упоминание 'age' или 'возраст'"
        );
        verify(userDAO, never()).saveUser(any(User.class));
    }

    @Test
    void testCreateUser_DAOSaveThrowsException(){
        doThrow(new RuntimeException("Database error")).when(userDAO).saveUser(any(User.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.createUser(TEST_NAME, TEST_EMAIL, TEST_AGE),
                "Ожидалось исключение от Дао при сохранении пользователя");
        assertTrue(
                exception.getMessage().contains("Database error"),
                "Сообщение исключения должно содержать 'Database error'"
        );

    }

    @Test
    void testGetUserById_Success() {
        when(userDAO.getUserById(TEST_USER_ID)).thenReturn(testUser);

        User result = userService.getUserById(TEST_USER_ID);


        assertNotNull(result, "Результат не должен быть null при успешном получении пользователя");

        assertEquals(TEST_USER_ID, result.getId(), "ID пользователя должен совпадать с запрошенным");



        assertEquals(TEST_NAME.trim(), result.getName(), "Имя должно быть обработано (trim)");
        assertEquals(TEST_EMAIL.toLowerCase(), result.getEmail(), "Email должен быть в нижнем регистре");
        assertEquals(TEST_AGE, result.getAge(), "Возраст должен совпадать");

        verify(userDAO, times(1)).getUserById(eq(TEST_USER_ID));
    }


    @Test
    void testGetUserById_UserNotFound_ThrowsException() {
        when(userDAO.getUserById(TEST_USER_ID)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(TEST_USER_ID),
                "Ожидалось исключение IllegalArgumentException при отсутствии пользователя"
        );


        assertTrue(
                exception.getMessage().contains(String.valueOf(TEST_USER_ID)),
                "Сообщение исключения должно содержать ID пользователя"
        );


        verify(userDAO, times(1)).getUserById(eq(TEST_USER_ID));
    }

    @Test
    void testGetUserById_DAOReadThrowsException() {

        doThrow(new RuntimeException("Database read error"))
                .when(userDAO).getUserById(TEST_USER_ID);


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserById(TEST_USER_ID),
                "Ожидалось исключение от DAO при чтении пользователя"
        );


        assertTrue(
                exception.getMessage().contains("Database read error"),
                "Сообщение исключения должно содержать 'Database read error'"
        );


        verify(userDAO, times(1)).getUserById(eq(TEST_USER_ID));
    }

    @Test
    void testUpdateUser_Success(){


        when(userDAO.getUserById(TEST_USER_ID)).thenReturn(testUser);
        User result = userService.updateUser(TEST_USER_ID, TEST_NAME,TEST_EMAIL,TEST_AGE);
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getId());
        assertEquals(TEST_NAME.trim(), result.getName());
        assertEquals(TEST_EMAIL.toLowerCase(), result.getEmail());
        assertEquals(TEST_AGE, result.getAge());
        verify(userDAO, times(1)).getUserById(eq(TEST_USER_ID));
        verify(userDAO, times(1)).updateUser(argThat(user ->
                user.getId().equals(TEST_USER_ID) &&
                        user.getName().equals(TEST_NAME.trim()) &&
                        user.getEmail().equals(TEST_EMAIL.toLowerCase()) &&
                        user.getAge().equals(TEST_AGE)
        ));
    }
    @Test
    void testUpdateUser_UpdateThrowsException() {

        when(userDAO.getUserById(TEST_USER_ID)).thenReturn(testUser);
        doThrow(new RuntimeException("Database update error"))
                .when(userDAO).updateUser(any(User.class));


        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(TEST_USER_ID, TEST_NAME, TEST_EMAIL, TEST_AGE),
                "Ожидалось исключение от DAO при обновлении пользователя"
        );


        assertTrue(
                exception.getMessage().contains("Database update error"),
                "Сообщение исключения должно содержать 'Database update error'"
        );


        verify(userDAO, times(1)).getUserById(eq(TEST_USER_ID));

        verify(userDAO, times(1)).updateUser(any(User.class));
    }

    @Test
    void testGetAllUsers_Success() {
        // Given: готовим тестовые данные
        User user1 = new User("test1", "test1@mail.com", 21);
        user1.setId(2L);
        User user2 = new User("test2", "test2@mail.com", 22);
        user2.setId(3L);
        User user3 = new User("test3", "test3@mail.com", 23);
        user3.setId(4L);

        List<User> expectedUsers = Arrays.asList(user1, user2, user3);


        when(userDAO.getAllUsers()).thenReturn(expectedUsers);


        List<User> result = userService.getAllUsers();

        assertNotNull(result, "Результат не должен быть null при успешном получении всех пользователей");
        assertEquals(expectedUsers.size(), result.size(),
                "Размер списка пользователей должен совпадать с ожидаемым");

        // Проверяем, что все пользователи присутствуют и корректны
        for (int i = 0; i < expectedUsers.size(); i++) {
            User expected = expectedUsers.get(i);
            User actual = result.get(i);

            assertNotNull(actual);
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getName(), actual.getName());
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getAge(), actual.getAge());
        }

        // Then: проверяем вызов DAO
        verify(userDAO, times(1)).getAllUsers();
    }

    @Test
    void testGetAllUsers_DAOReadThrowsException(){

    }





}
