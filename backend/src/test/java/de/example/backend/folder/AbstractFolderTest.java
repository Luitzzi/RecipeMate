package de.example.backend.folder;

import de.example.backend.boundary.dto.FolderUiDTO;
import de.example.backend.boundary.dto.recipe.ui.RecipeUiDTO;
import de.example.backend.entity.FolderEntity;
import de.example.backend.entity.recipe.RecipeEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.recipe.DataSchemaTestUtils;
import de.example.backend.repository.FolderRepository;
import de.example.backend.repository.RecipeRepository;
import de.example.backend.service.FolderService;
import de.example.backend.service.RecipeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Set;
import java.util.stream.Collectors;

import static de.example.backend.recipe.RecipeTestData.createRecipeUiDTO;

/**
 * Shared setup for folder tests: two users and helpers to create folders and recipes.
 */
@SpringBootTest
@Transactional
abstract class AbstractFolderTest {

    @Autowired
    protected FolderService folderService;

    @Autowired
    protected RecipeService recipeService;

    @Autowired
    protected FolderRepository folderRepository;

    @Autowired
    protected RecipeRepository recipeRepository;

    @Autowired
    protected DataSchemaTestUtils dataSchemaTestUtils;

    @Autowired
    protected EntityManager entityManager;

    // Same bean override as AbstractRecipeServiceTest so the Spring context is shared
    @MockitoBean
    protected S3Client s3Client;

    protected UserEntity owner;
    protected UserEntity otherUser;

    @BeforeEach
    void setUpUsers() {
        owner = new UserEntity();
        otherUser = new UserEntity();
        entityManager.persist(owner);
        entityManager.persist(otherUser);
        dataSchemaTestUtils.flushAndClear();
    }

    protected FolderUiDTO createFolder(String name, UserEntity user) {
        FolderUiDTO folder = new FolderUiDTO();
        folder.setName(name);
        return folderService.create(folder, user);
    }

    protected Long createRecipe(String title, UserEntity user) {
        RecipeUiDTO recipe = createRecipeUiDTO();
        recipe.setTitle(title);
        return recipeService.create(recipe, user).getId();
    }

    protected Set<Long> recipeIdsInFolder(Long folderId) {
        dataSchemaTestUtils.flushAndClear();
        FolderEntity folder = folderRepository.findById(folderId).orElseThrow();
        return folder.getRecipes().stream()
                .map(RecipeEntity::getId)
                .collect(Collectors.toSet());
    }
}
