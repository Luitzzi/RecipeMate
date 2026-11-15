package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.user.UserUiDTO;
import de.example.backend.entity.Language;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Read and update the profile of the current user")
public class UserUiController {

    private final UserService userService;

    @GetMapping("/preferences")
    @Operation(summary = "Get the preferences of the current user")
    public UserUiDTO getPreferences(@RequestParam Language language) {
        return userService.getPreferences(currentUserId(), language);
    }

    @PutMapping("/preferences")
    @Operation(summary = "Replace the preferences of the current user")
    public UserUiDTO updatePreferences(
            @RequestBody @Valid UserUiDTO userUiDTO,
            @RequestParam Language language) {
        return userService.updatePreferences(currentUserId(), userUiDTO, language);
    }

    // TODO: Replace with the user id from the Zitadel token once OAuth is set up
    private UUID currentUserId() {
        return UserService.TEST_USER.getId();
    }
}
