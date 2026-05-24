package com.example.controller;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    public void testGetAllUsers() throws Exception {
        // Given
        User user1 = new User("Иван Иванов", "ivan@example.com", 30);
        user1.setId(1L);
        User user2 = new User("Мария Сидорова", "maria@example.com", 25);
        user2.setId(2L);

        UserCreateDto dto1 = new UserCreateDto("Иван Иванов", "ivan@example.com", 30);
        dto1.setId(user1.getId());
        UserCreateDto dto2 = new UserCreateDto("Мария Сидорова", "maria@example.com", 25);
        dto2.setId(user2.getId());

        // Настройка моков
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toCreateDto(user1)).thenReturn(dto1);
        when(userMapper.toCreateDto(user2)).thenReturn(dto2);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"))
                .andExpect(jsonPath("$[0].age").value(30))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Мария Сидорова"))
                .andExpect(jsonPath("$[1].email").value("maria@example.com"))
                .andExpect(jsonPath("$[1].age").value(25));
    }

    @Test
    public void testCreateUser() throws Exception {
        // Given
        UserCreateDto requestDto = new UserCreateDto("Анна Петрова", "anna@example.com", 28);

        User userEntity = new User("Анна Петрова", "anna@example.com", 28);
        userEntity.setId(1L);

        UserCreateDto responseDto = new UserCreateDto("Анна Петрова", "anna@example.com", 28);
        responseDto.setId(1L);

        // Настройка моков
        when(userService.createUser(requestDto)).thenReturn(userEntity);
        when(userMapper.toCreateDto(userEntity)).thenReturn(responseDto);

        verify(userMapper, never()).toEntity(any());

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Анна Петрова\",\"email\":\"anna@example.com\",\"age\":28}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Анна Петрова"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(28));
    }

    @Test
    public void testGetUserById() throws Exception {
        // Given
        Long userId = 1L;
        User userEntity = new User("Анна Петрова", "anna@example.com", 28);
        userEntity.setId(userId);

        UserUpdateDto responseDto = new UserUpdateDto();
        responseDto.setName("Анна Петрова");
        responseDto.setEmail("anna@example.com");
        responseDto.setAge(28);
        responseDto.setId(userId);

        // Настройка моков
        when(userService.getUserById(userId)).thenReturn(userEntity);
        when(userMapper.toUpdateDto(userEntity)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Петрова"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(28));
    }


    @Test
    public void testGetUserById_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // Настройка моков
        when(userService.getUserById(nonExistentId)).thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));

        // When & Then
        mockMvc.perform(get("/api/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999 не найден"));
    }
    @Test
    public void testUpdateUser() throws Exception {
        // Given
        Long userId = 1L;
        UserUpdateDto requestDto = new UserUpdateDto();
        requestDto.setName("Анна Петрова Обновлённая");
        requestDto.setEmail("updated@example.com");
        requestDto.setAge(29);
        requestDto.setId(userId);

        User existingUser = new User("Анна Петрова", "anna@example.com", 28);
        existingUser.setId(userId);

        User updatedUser = new User("Анна Петрова Обновлённая", "updated@example.com", 29);
        updatedUser.setId(userId);

        UserUpdateDto responseDto = new UserUpdateDto();
        responseDto.setName("Анна Петрова Обновлённая");
        responseDto.setEmail("updated@example.com");
        responseDto.setAge(29);
        responseDto.setId(userId);

        // Настройка моков
        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.updateUser(userId, requestDto)).thenReturn(updatedUser);
        when(userMapper.toUpdateDto(updatedUser)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Анна Петрова Обновлённая\",\"email\":\"updated@example.com\",\"age\":29,\"id\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Петрова Обновлённая"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(29));
    }

    @Test
    public void testUpdateUser_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        UserUpdateDto requestDto = new UserUpdateDto();
        requestDto.setName("Неизвестный");
        requestDto.setEmail("unknown@example.com");
        requestDto.setAge(30);

        // Настройка моков
        when(userService.getUserById(nonExistentId)).thenThrow(new UserNotFoundException("Пользователь с ID 999 не найден"));

        // When & Then
        mockMvc.perform(put("/api/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Неизвестный\",\"email\":\"unknown@example.com\",\"age\":30}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999 не найден"));
    }
    @Test
    public void testDeleteUser() throws Exception {
        // Given
        Long userId = 1L;

        // Настройка моков
        when(userService.deleteUser(userId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
    @Test
    public void testDeleteUser_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // Настройка моков
        when(userService.deleteUser(nonExistentId)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999 не найден"));
    }
    @Test
    public void testGetAllUsers_EmptyList() throws Exception {
        // Given: подготовка тестовых данных — пустой список пользователей
        when(userService.getAllUsers()).thenReturn(List.of());

        // When & Then: выполнение запроса и проверка
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testCreateUser_ValidationError_EmptyFields() throws Exception {
        // Given: подготовка некорректных данных (пустые имя и email, отрицательный возраст)
        String invalidJson = "{\"name\":\"\",\"email\":\"\",\"age\":-5}";


        // When & Then: выполнение запроса и проверка
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Имя не может быть пустым"))
                .andExpect(jsonPath("$.email").value("Email  не может быть пустым"))
                .andExpect(jsonPath("$.age").value("Возраст должен быть неотрицательным числом"));
    }

    @Test
    public void testCreateUser_ValidationError_InvalidEmail() throws Exception {
        // Given: некорректный email
        String invalidJson = "{\"name\":\"Анна Петрова\",\"email\":\"invalid-email\",\"age\":28}";


        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email должен быть корректным"));
    }
    @Test
    public void testUpdateUser_ValidationError_NegativeAge() throws Exception {
        // Given
        Long userId = 1L;
        String invalidJson = "{\"name\":\"Анна\",\"email\":\"anna@example.com\",\"age\":-10}";

        // Настройка моков
        User existingUser = new User("Анна", "anna@example.com", 28);
        existingUser.setId(userId);
        when(userService.getUserById(userId)).thenReturn(existingUser);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.age").value("Возраст должен быть неотрицательным числом"));
    }
    @Test
    public void testGetUserById_NullId() throws Exception {
        // Given
        Long nullId = null;

        // When & Then
        mockMvc.perform(get("/api/users/{id}", nullId))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDeleteUser_NullId() throws Exception {
        // Given
        Long nullId = null;

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", nullId))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testGetAllUsers_SingleUser() throws Exception {
        // Given: один пользователь
        User user = new User("Иван Иванов", "ivan@example.com", 30);
        user.setId(1L);

        UserCreateDto dto = new UserCreateDto("Иван Иванов", "ivan@example.com", 30);
        dto.setId(user.getId());

        // Настройка моков
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userMapper.toCreateDto(user)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"))
                .andExpect(jsonPath("$[0].age").value(30));
    }
    @Test
    public void testUpdateUser_PartialUpdate() throws Exception {
        // Given
        Long userId = 1L;
        UserUpdateDto requestDto = new UserUpdateDto();
        requestDto.setEmail("updated@example.com");

        User existingUser = new User("Анна Петрова", "anna@example.com", 28);
        existingUser.setId(userId);

        User updatedUser = new User("Анна Петрова", "updated@example.com", 28);
        updatedUser.setId(userId);

        UserUpdateDto responseDto = new UserUpdateDto();
        responseDto.setName("Анна Петрова");
        responseDto.setEmail("updated@example.com");
        responseDto.setAge(28);
        responseDto.setId(userId);

        // Настройка моков
        when(userService.getUserById(userId)).thenReturn(existingUser);
        when(userService.updateUser(userId, requestDto)).thenReturn(updatedUser);
        when(userMapper.toUpdateDto(updatedUser)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Анна Петрова"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.age").value(28));
    }







}

