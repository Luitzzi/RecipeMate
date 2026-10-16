package de.example.backend.entity.recipe;

import jakarta.persistence.*;

@Entity
@Table(name = RecipePropertyBE.tableName)
public class RecipePropertyBE {
    public static final String tableName = "recipe_properties";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}

