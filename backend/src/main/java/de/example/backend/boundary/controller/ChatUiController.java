package de.example.backend.boundary.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import de.example.backend.boundary.uidto.chat.ChatRequestUiDTO;
import de.example.backend.boundary.uidto.chat.ChatResponseUiDTO;
import de.example.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatUiController {

    private final ChatService chatBA;

    @PostMapping("/")
    @Operation(summary = "Create a new chat")
    UUID createChat() {
        return this.chatBA.startChat();
    }

    @PostMapping("/{chatId}")
    @Operation(summary = "Send a message to an existing chat")
    @ApiResponse(responseCode = "404", description = "Chat not found")
    @ApiResponse(responseCode = "502", description = "Chat response failed unexpectedly")
    ResponseEntity<ChatResponseUiDTO> sendMessage(
            @PathVariable UUID chatId,
            @Valid @RequestBody ChatRequestUiDTO request) {
        return ResponseEntity.ok(this.chatBA.sendMessage(request, chatId));
    }
}
