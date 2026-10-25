package de.example.backend.entity.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = RecipePropertyEntity.tableName)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipePropertyEntity {
    public static final String tableName = "properties";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}

