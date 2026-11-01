package de.example.backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.example.backend.boundary.dto.user.UserUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.UserRepository;
import de.example.backend.repository.query.UserQueries;
import de.example.backend.service.exception.user.UserNotFoundException;
import de.example.backend.service.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public static final UserEntity TEST_USER = new UserEntity(
            UUID.fromString("00000000-0000-0000-0000-000000000000"));

    private final UserRepository userRepository;
    private final UserQueries userQueries;
    private final UserMapper userMapper;

    @Transactional
    public void createTestUserIfMissing() {
        if (!userRepository.existsById(TEST_USER.getId())) {
            userQueries.insert(TEST_USER);
            log.info("Created the test user {}", TEST_USER.getId());
        } else {
            log.info("Skipping creating the test user. It already exists.");
        }
    }

    @Transactional
    public UserUiDTO getPreferences(UUID userId, Language language) {
        return userMapper.toUiDTO(load(userId), language);
    }

    /**
     * The user is managed by Hibernate inside the transaction,
     * so the changes are written to the DB on commit without an explicit save.
     */
    @Transactional
    public UserUiDTO updatePreferences(UUID userId, UserUiDTO userUiDTO, Language language) {
        UserEntity userEntity = load(userId);
        userMapper.updateEntity(userUiDTO, userEntity);
        return userMapper.toUiDTO(userEntity, language);
    }

    private UserEntity load(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
