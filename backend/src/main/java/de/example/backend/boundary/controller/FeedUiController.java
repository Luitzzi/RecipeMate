package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.PageUiDTO;
import de.example.backend.boundary.dto.feed.FeedSectionType;
import de.example.backend.boundary.dto.feed.FeedSectionUiDTO;
import de.example.backend.boundary.dto.feed.RecipeSummaryUiDTO;
import de.example.backend.entity.recipe.RecipeProperty;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.FeedService;
import de.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Recipe sections of the feed page")
public class FeedUiController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final FeedService feedService;

    @GetMapping
    @Operation(summary = "Get all non-empty feed sections in feed order with their first page")
    public List<FeedSectionUiDTO> getSections() {
        UserEntity owner = UserService.TEST_USER;
        return feedService.getSections(owner, DEFAULT_PAGE_SIZE);
    }

    @GetMapping("/sections")
    @Operation(summary = "Get a page of the recipes of one feed section")
    public PageUiDTO<RecipeSummaryUiDTO> getSectionPage(
            @RequestParam FeedSectionType type,
            @RequestParam(required = false) @Nullable RecipeProperty property,
            @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable) {
        UserEntity owner = UserService.TEST_USER;
        return feedService.getSectionPage(type, property, owner, pageable);
    }
}
