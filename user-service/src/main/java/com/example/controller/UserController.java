package com.example.controller;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/users")
@io.swagger.v3.oas.annotations.tags.Tag(name = "User API", description = "Операции по управлению пользователями")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей с ссылками на каждый ресурс"
    )
    public CollectionModel<EntityModel<UserCreateDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<EntityModel<UserCreateDto>> dtoList = users.stream()
                .map(user -> {
                    UserCreateDto dto = userMapper.toCreateDto(user);
                    EntityModel<UserCreateDto> resource = EntityModel.of(dto);
                    resource.add(linkTo(methodOn(UserController.class)
                            .getUserById(user.getId()))
                            .withSelfRel());
                    return resource;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserCreateDto>> resources = CollectionModel.of(dtoList);
        resources.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
        return resources;
    }



    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе по его ID с ссылкой на сам ресурс"
    )
    public ResponseEntity<EntityModel<UserUpdateDto>> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        UserUpdateDto dto = userMapper.toUpdateDto(user);
        EntityModel<UserUpdateDto> resource = EntityModel.of(dto);
        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(id))
                .withSelfRel());
        return ResponseEntity.ok(resource);
    }



    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Создать нового пользователя",
            description = "Создаёт нового пользователя и возвращает его с ссылкой на ресурс"
    )
    public ResponseEntity<EntityModel<UserCreateDto>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя"
            )
            @Valid @RequestBody UserCreateDto userDto) {

        User createdUser = userService.createUser(userDto);
        UserCreateDto responseDto = userMapper.toCreateDto(createdUser);

        EntityModel<UserCreateDto> resource = EntityModel.of(responseDto);
        resource.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }


    @PutMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по ID"
    )
    public ResponseEntity<EntityModel<UserUpdateDto>> updateUser(
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновлённые данные пользователя"
            )
            @Valid @RequestBody UserUpdateDto userDto) {
        User existingUser = userService.getUserById(id);
        userMapper.updateEntityFromDto(userDto, existingUser);
        User updatedUser = userService.updateUser(id, userDto);
        UserUpdateDto responseDto = userMapper.toUpdateDto(updatedUser);
        EntityModel<UserUpdateDto> resource = EntityModel.of(responseDto);
        resource.add(linkTo(methodOn(UserController.class)
                .getUserById(updatedUser.getId()))
                .withSelfRel());
        return ResponseEntity.ok(resource);
    }


    @DeleteMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID"
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (!isDeleted) {
            throw new UserNotFoundException("Пользователь с ID " + id + " не найден");
        }
        return ResponseEntity.noContent().build();
    }
}
