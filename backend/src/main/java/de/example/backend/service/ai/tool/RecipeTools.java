package de.example.backend.service.ai.tool;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.tool.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;
import de.example.backend.boundary.dto.recipe.ai.RecipeAiDTO;
import de.example.backend.boundary.dto.recipe.ai.RecipeValidationAiDTO;
import de.example.backend.entity.Language;
import de.example.backend.service.RecipeService;
import de.example.backend.service.UserService;
import de.example.backend.service.ai.ChatResponseUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RecipeTools {

    static final BeanOutputConverter<RecipeValidationAiDTO> VALIDATION_CONVERTER = new BeanOutputConverter<>(
            RecipeValidationAiDTO.class);

    private final ChatClient validatorChatClient;
    private final Validator validator;
    private final JsonMapper jsonMapper;

    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeTools(
            ChatClient.Builder validatorBuilder,
            @Value("classpath:prompts/recipe-validator.md") Resource validatorSystemPrompt,
            Validator validator,
            JsonMapper jsonMapper,
            RecipeService recipeService,
            UserService userService) {

        this.validatorChatClient = validatorBuilder
                .defaultSystem(validatorSystemPrompt)
                // Native structured output, the response text is guaranteed to match the schema
                .defaultOptions(AnthropicChatOptions.builder()
                        .outputSchema(VALIDATION_CONVERTER.getJsonSchema()))
                .build();

        this.validator = validator;
        this.jsonMapper = jsonMapper;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @Tool(name = "create-recipe", description = "Creates a recipe based on the given input, and returns the ID of the created recipe.")
    public Long createRecipe(RecipeAiDTO recipe) {
        // * Schema check (JSON Schema)
        Set<ConstraintViolation<RecipeAiDTO>> violations = validator.validate(recipe);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            log.warn("AI-created recipe failed schema validation: {}", errors);
            throw new IllegalArgumentException("Recipe schema validation failed: " + errors);
        }

        String recipeJson = jsonMapper.writeValueAsString(recipe);

        // * Semantic check (validator Agent)
        ChatResponse response = validatorChatClient
                .prompt()
                .user(recipeJson)
                .call()
                .chatResponse();
        RecipeValidationAiDTO validationResult = VALIDATION_CONVERTER.convert(ChatResponseUtils.firstText(response));
        if (validationResult.getFindings().size() > 0) {
            String errors = String.join(", ", validationResult.getFindings());
            log.warn("AI-created recipe failed semantic validation: {}", errors);
            throw new IllegalArgumentException("Recipe semantic validation failed: " + errors);
        }

        // * Create the recipe in the database
        Long recipeId = recipeService.createFromAi(recipe, UserService.TEST_USER);
        return recipeId;

        // TODO JPI: into docs: decision: create recipe and make chatbot output link,
        // because: simpler (in more details!) @Johannes @Luis
    }

    @Tool(name = "get-recipe", description = "Retrieves a recipe by its ID.")
    public RecipeAiDTO getRecipe(Long recipeId) {
        // TODO JPI: Language into Recipe, then use it here!
        return recipeService.getForAi(recipeId, Language.EN)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
    }
}
