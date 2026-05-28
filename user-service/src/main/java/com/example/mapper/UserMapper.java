package com.example.mapper;

import com.example.dto.UserCreateDto;
import com.example.dto.UserUpdateDto;
import com.example.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public User toEntity(UserCreateDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        return user;
    }


    public void updateEntityFromDto(UserUpdateDto dto, User entity) {
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            entity.setAge(dto.getAge());
        }
    }


    public UserCreateDto toCreateDto(User user) {
        UserCreateDto dto = new UserCreateDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }


    public UserUpdateDto toUpdateDto(User user) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        return dto;
    }
}
