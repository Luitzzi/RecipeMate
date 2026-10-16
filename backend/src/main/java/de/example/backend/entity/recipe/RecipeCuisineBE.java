package de.example.backend.entity.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = RecipeCuisineBE.tableName)
@Getter
@Setter
public class RecipeCuisineBE {
    public static final String tableName = "recipe_cuisines";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
