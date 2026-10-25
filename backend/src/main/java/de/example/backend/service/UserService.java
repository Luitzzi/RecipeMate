package de.example.backend.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import de.example.backend.entity.UserEntity;
import de.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final UUID MOCK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final UserRepository userRepository;

    public UserEntity getMockUser() {
        // INFO: on deleting this method, also look at UserRepository!

        Optional<UserEntity> user = userRepository.findById(MOCK_USER_ID);
        if (user.isPresent()) {
            return user.get();
        }

        userRepository.insertIfAbsent(MOCK_USER_ID);
        return userRepository.findById(MOCK_USER_ID).orElseThrow();
    }

}
