package com.example.service;

import com.example.dao.UserDAO;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
