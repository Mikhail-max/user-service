package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UserCreateDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email  не может быть пустым")
    private String email;
    @PositiveOrZero(message = "Возраст должен быть неотрицательным числом")
    private Integer age;

    public UserCreateDto(){};
    public UserCreateDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

}
