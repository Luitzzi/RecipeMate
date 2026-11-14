package de.example.backend.service;

import de.example.backend.boundary.dto.chat.ChatRequestUiDTO;
import de.example.backend.service.chat.ChatResponseUtils;
import de.example.backend.service.chat.advisor.CurrentViewAdvisor;
import de.example.backend.service.chat.tool.RecipeTools;
import de.example.backend.service.chat.tool.UnitTools;
import de.example.backend.service.exception.chat.ChatNotFoundException;
import de.example.backend.service.exception.chat.ChatResponseFailedException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
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

    // Cache that tracks the IDs of active chats and cleans up the chat memory after
    // chat expiration.
    private final Cache<UUID, Boolean> activeChats;

    public ChatService(ChatClient.Builder builder,
            @Value("classpath:prompts/mate.md") Resource systemPrompt,
            UnitTools unitTools,
            RecipeTools recipeTools) {

        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(200)
                .build();

        this.activeChats = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .removalListener((UUID id, Boolean v, RemovalCause cause) -> this.chatMemory
                        .clear(id.toString()))
                .build();

        this.chatClient = builder
                // Set the system prompt for the chat client.
                .defaultSystem(systemPrompt)
                // Advisors ("middlewares")
                .defaultAdvisors(
                        // Injects the current view of the UI into the chat context.
                        new CurrentViewAdvisor()
                                .setOrder(ToolCallingAdvisor.DEFAULT_ORDER - 100), // low order => runs early
                        // Stores and injects the conversation history.
                        MessageChatMemoryAdvisor.builder(this.chatMemory)
                                .order(ToolCallingAdvisor.DEFAULT_ORDER + 100) // high order => runs late
                                .build())
                // Tools (can be called by the LLM)
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

    public String sendMessage(ChatRequestUiDTO request, UUID chatId) {
        if (this.activeChats.getIfPresent(chatId) == null) {
            throw new ChatNotFoundException(chatId);
        }

        ChatResponse response = this.chatClient.prompt()
                // Advisor parameters (current view & conversation ID)
                .advisors(advisor -> advisor
                        .param(CurrentViewAdvisor.CURRENT_VIEW, request.getCurrentView())
                        .param(ChatMemory.CONVERSATION_ID, chatId.toString()))
                .user(request.getMessage())
                .call()
                .chatResponse();

        boolean responseSuccess = isResponseSuccess(response);
        if (!responseSuccess)
            throw new ChatResponseFailedException();
        return extractAnswer(response);
    }

    private static String extractAnswer(ChatResponse response) {
        return ChatResponseUtils.texts(response)
                .collect(Collectors.joining("\n\n"));
    }

    private static boolean isResponseSuccess(ChatResponse response) {
        String reason = response.getResult().getMetadata().getFinishReason();
        return switch (reason.toLowerCase()) {
            case "stop_sequence", "end_turn" -> true;
            default -> false;
        };
    }
}
