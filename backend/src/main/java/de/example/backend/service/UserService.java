package de.example.backend.service;

import de.example.backend.entity.UserEntity;
import de.example.backend.repository.UserRepository;
import de.example.backend.repository.querydsl.UserQueries;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public static final UserEntity TEST_USER = new UserEntity(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            null, null);

    private final UserRepository userRepository;
    private final UserQueries userQueries;

    @Transactional
    public void createTestUserIfMissing() {
        if (!userRepository.existsById(TEST_USER.getId())) {
            userQueries.insert(TEST_USER);
            log.info("Created the test user {}", TEST_USER.getId());
        } else {
            log.info("Skipping creating the test user. It already exists.");
        }
    }
}
