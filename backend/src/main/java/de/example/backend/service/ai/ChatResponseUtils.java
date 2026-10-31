package de.example.backend.service.ai;

import java.util.stream.Stream;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ChatResponseUtils {

    // Inspired by
    // https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html#_non_streaming_example

    public static Stream<String> texts(ChatResponse response) {
        if (response == null) {
            return Stream.empty();
        }
        return response.getResults().stream()
                .map(result -> result.getOutput())
                .filter(message -> !isThinking(message))
                .map(message -> message.getText())
                .filter(text -> text != null && !text.isBlank());
    }

    public static String firstText(ChatResponse response) {
        return texts(response)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No text found in response"));
    }

    private static boolean isThinking(AssistantMessage message) {
        return message.getMetadata().containsKey("signature")
                || message.getMetadata().containsKey("data");
    }
}
