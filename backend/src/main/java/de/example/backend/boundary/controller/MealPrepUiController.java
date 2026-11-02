package de.example.backend.boundary.controller;

import de.example.backend.boundary.dto.mealprep.MealPlanDayUiDTO;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.service.MealPlanService;
import de.example.backend.service.UserService;
import de.example.backend.service.exception.InvalidMealPlanException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prepping")
@RequiredArgsConstructor
@Tag(name = "Meal Prepping", description = "Plan recipes and shopping trips per day")
public class MealPrepUiController {

    private final MealPlanService mealPlanService;

    @GetMapping
    @Operation(summary = "Get all planned days of an user in an inclusive date range")
    public List<MealPlanDayUiDTO> getRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        UserEntity owner = UserService.TEST_USER;
        return mealPlanService.getRange(owner, from, to);
    }

    @PutMapping("/{date}")
    @Operation(summary = "Create or replace the plan of a day. Empty days are deleted")
    public MealPlanDayUiDTO save(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody @Valid MealPlanDayUiDTO mealPlanDayUiDTO) {
        if (!date.equals(mealPlanDayUiDTO.getDate()))
            throw new InvalidMealPlanException("Path date " + date + " does not match body date " + mealPlanDayUiDTO.getDate());

        UserEntity owner = UserService.TEST_USER;
        return mealPlanService.save(mealPlanDayUiDTO, owner);
    }
}
