package de.example.backend.service;

import de.example.backend.boundary.uidto.ChatRequestUiDTO;
import de.example.backend.boundary.uidto.ChatResponseUiDTO;
import de.example.backend.boundary.uidto.ChatResponseUiDTO.ChatResponseStatus;
import de.example.backend.service.ai.advisor.CurrentViewAdvisor;
import de.example.backend.service.ai.tool.UnitTools;

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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatBA {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final Cache<UUID, Boolean> activeChats;

    public ChatBA(ChatClient.Builder builder,
            @Value("classpath:prompts/mate.md") Resource systemPrompt) {
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        this.activeChats = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .removalListener((UUID id, Boolean v, RemovalCause cause) -> this.chatMemory.clear(id.toString()))
                .build();

        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(this.chatMemory)
                                .order(ToolCallingAdvisor.DEFAULT_ORDER + 100)
                                .build(),
                        new CurrentViewAdvisor().setOrder(ToolCallingAdvisor.DEFAULT_ORDER - 100))
                .defaultTools(
                        new UnitTools())
                .build();
    }

    public UUID startChat() {
        UUID chatId = UUID.randomUUID();
        activeChats.put(chatId, true);
        return chatId;
    }

    public Optional<ChatResponseUiDTO> sendMessage(ChatRequestUiDTO request, UUID chatId) {
        if (this.activeChats.getIfPresent(chatId) == null) {
            return Optional.empty();
        }

        ChatResponse response = this.chatClient.prompt()
                .advisors(a -> a
                        .param(CurrentViewAdvisor.CURRENT_VIEW, request.getCurrentView())
                        .param(ChatMemory.CONVERSATION_ID, chatId.toString()))
                .user(request.getMessage())
                .call()
                .chatResponse();

        return Optional.of(
                new ChatResponseUiDTO(
                        extractAnswer(response),
                        extractStatus(response)));
    }

    // Taken from
    // https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html#_non_streaming_example
    private static String extractAnswer(ChatResponse response) {
        if (response == null) {
            return "";
        }
        return response.getResults().stream()
                .map(g -> g.getOutput())
                .filter(message -> !isThinking(message))
                .map(m -> m.getText())
                .filter(text -> text != null && !text.isBlank())
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

    private static boolean isThinking(AssistantMessage message) {
        return message.getMetadata().containsKey("signature")
                || message.getMetadata().containsKey("data");
    }
}