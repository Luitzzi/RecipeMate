package de.example.backend.entity.user;

import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.recipe.RecipeImageEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = UserEntity.tableName)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    public static final String tableName = "users";

    @Id
    @GeneratedValue
    private UUID id;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "createdBy")
    Set<IngredientEntity> ingredients = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "owner")
    Set<RecipeEntity> recipes = new HashSet<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "createdBy")
    Set<RecipeImageEntity> images = new HashSet<>();
}
