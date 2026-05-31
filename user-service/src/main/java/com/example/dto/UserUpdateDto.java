package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UserUpdateDto {
    private Long id;
    private String name;
    @Email(message = "Email должен быть корректным")
    private String email;
    @PositiveOrZero(message = "Возраст должен быть неотрицательным числом")
    private Integer age;
}
