package de.example.backend.boundary.dto.mealprep;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MealPlanDayUiDTO {

    @NotNull
    private LocalDate date;

    // TODO: IF CHECKED ADD ALL INGREDIENTS TO THE SHOPPING LIST
    private boolean goShopping;

    // TODO: CURRENTLY MORE THAN ONE RECIPE PER DAY. ORDER OF THE ARRAY IS THE ORDER DISPLAYED.
    @NotNull
    private List<@NotNull Long> recipeIds = new ArrayList<>();
}
