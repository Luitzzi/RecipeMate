package de.example.backend.entity;

import de.example.backend.entity.ingredient.IngredientBE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = UserBE.tableName)
@Getter
@Setter
@NoArgsConstructor
public class UserBE {
    public static final String tableName = "users";

    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(
            mappedBy = "createdBy",
            cascade = CascadeType.ALL, orphanRemoval = true)
    Set<IngredientBE> ingredients = new HashSet<>();

}
