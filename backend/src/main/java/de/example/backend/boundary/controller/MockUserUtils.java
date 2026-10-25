package de.example.backend.boundary.controller;

import de.example.backend.entity.UserEntity;

import java.util.UUID;

public class MockUserUtils {
    public static UserEntity getMockUser() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        return userEntity;
    }
}
