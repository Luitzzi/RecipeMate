package de.example.backend.entity.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = RecipeCuisineEntity.tableName)
@Getter
@Setter
public class RecipeCuisineEntity {
    public static final String tableName = "cuisines";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
