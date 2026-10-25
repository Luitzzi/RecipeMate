package de.example.backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.example.backend.entity.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    public UserEntity getMockUser() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        return userEntity;
    }
    
}
