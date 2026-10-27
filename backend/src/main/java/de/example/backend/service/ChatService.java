package de.example.backend.service;

import de.example.backend.boundary.dto.chat.ChatRequestUiDTO;
import de.example.backend.boundary.dto.chat.ChatResponseUiDTO;
import de.example.backend.boundary.dto.chat.ChatResponseUiDTO.ChatResponseStatus;
import de.example.backend.service.ai.ChatResponseUtils;
import de.example.backend.service.ai.advisor.CurrentViewAdvisor;
import de.example.backend.service.ai.tool.RecipeTools;
import de.example.backend.service.ai.tool.UnitTools;
import de.example.backend.service.exception.chat.ChatNotFoundException;
import de.example.backend.service.exception.chat.ChatResponseFailedException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.*;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final Cache<UUID, Boolean> activeChats;

    // TODO JPI: Docstrings
    public ChatService(ChatClient.Builder builder,
            @Value("classpath:prompts/mate.md") Resource systemPrompt,
            UnitTools unitTools,
            RecipeTools recipeTools) {

        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        this.activeChats = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .removalListener((UUID id, Boolean v, RemovalCause cause) -> this.chatMemory
                        .clear(id.toString()))
                .build();

        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(this.chatMemory)
                                .order(ToolCallingAdvisor.DEFAULT_ORDER + 100)
                                .build(),
                        new CurrentViewAdvisor()
                                .setOrder(ToolCallingAdvisor.DEFAULT_ORDER - 100))
                .defaultTools(
                        unitTools,
                        recipeTools)
                .build();
    }

    public UUID startChat() {
        UUID chatId = UUID.randomUUID();
        activeChats.put(chatId, true);
        return chatId;
    }

    public ChatResponseUiDTO sendMessage(ChatRequestUiDTO request, UUID chatId) {
        if (this.activeChats.getIfPresent(chatId) == null) {
            throw new ChatNotFoundException(chatId);
        }

        ChatResponse response = this.chatClient.prompt()
                .advisors(advisor -> advisor
                        .param(CurrentViewAdvisor.CURRENT_VIEW, request.getCurrentView())
                        .param(ChatMemory.CONVERSATION_ID, chatId.toString()))
                .user(request.getMessage())
                .call()
                .chatResponse();

        ChatResponseStatus status = extractStatus(response);
        if (status == ChatResponseStatus.ERROR) {
            throw new ChatResponseFailedException();
        }

        return new ChatResponseUiDTO(extractAnswer(response), status);
    }

    private static String extractAnswer(ChatResponse response) {
        return ChatResponseUtils.texts(response)
                .collect(Collectors.joining("\n\n"));
    }

    private static ChatResponseStatus extractStatus(ChatResponse response) {
        String reason = response.getResult().getMetadata().getFinishReason();
        return switch (reason == null ? "" : reason.toLowerCase()) {
            case "end_turn", "stop_sequence" -> ChatResponseStatus.SUCCESS;
            case "refusal" -> ChatResponseStatus.REFUSAL;
            default -> ChatResponseStatus.ERROR;
        };
    }
}
