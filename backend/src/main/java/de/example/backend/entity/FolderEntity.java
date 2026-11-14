package de.example.backend.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( //
        name = FolderEntity.tableName, //
        uniqueConstraints = @UniqueConstraint(columnNames = { "owner_id", "name" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderEntity {
    public static final String tableName = "folders";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @NotEmpty
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( //
            name = "folder_recipes", //
            joinColumns = @JoinColumn(name = "folder_id"), //
            inverseJoinColumns = @JoinColumn(name = "recipe_id"), //
            inverseForeignKey = @ForeignKey( //
                    name = "fk_folder_recipes_recipe", //
                    // Delete the join table entry when a recipe is deleted.
                    foreignKeyDefinition = "foreign key (recipe_id) references " + RecipeEntity.tableName
                            + " (id) on delete cascade"))
    @OnDelete(action = OnDeleteAction.CASCADE) // Delete the join table entry when a folder is deleted.
    @Setter(AccessLevel.NONE)
    private Set<@NotNull RecipeEntity> recipes = new HashSet<>();

    public void addRecipe(RecipeEntity recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(RecipeEntity recipe) {
        recipes.remove(recipe);
    }

}
