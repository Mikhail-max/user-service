package com.example.controller;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping
    public ResponseEntity<List<UserCreateDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserCreateDto> dtoList = users.stream()
                .map(userMapper::toCreateDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserUpdateDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserUpdateDto dto = userMapper.toUpdateDto(user);
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<UserCreateDto> createUser(@Valid @RequestBody UserCreateDto userDto) {

        User createdUser = userService.createUser(userDto);
        UserCreateDto responseDto = userMapper.toCreateDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserUpdateDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto) {
        User existingUser = userService.getUserById(id);
        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userService.updateUser(id, userDto);
        UserUpdateDto responseDto = userMapper.toUpdateDto(updatedUser);
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (!isDeleted) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return ResponseEntity.noContent().build();
    }
}
