package com.example.common.dto;

import lombok.Data;
@Data
public class UserEvent {
    private String operation;
    private String email;

    public UserEvent() {}

    public UserEvent(String operation, String email) {
        this.operation = operation;
        this.email = email;
    }

}
