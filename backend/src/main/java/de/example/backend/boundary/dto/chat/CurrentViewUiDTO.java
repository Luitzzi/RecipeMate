package de.example.backend.boundary.dto.chat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurrentViewUiDTO {

    public enum ViewType {
        NONE,
        RECIPE,
    }

    @NotNull
    private ViewType type;

    @Nullable
    private Long id;

    public String describe() {
        if (type == null || type == ViewType.NONE) {
            return "The user is not viewing anything specific.";
        }
        return String.format("The user is currently viewing a %s with ID %s.", type, id);
    }
}
