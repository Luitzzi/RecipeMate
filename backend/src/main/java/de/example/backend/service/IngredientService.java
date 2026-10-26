package de.example.backend.service;

import de.example.backend.boundary.uidto.IngredientUiDTO;
import de.example.backend.entity.IngredientType;
import de.example.backend.entity.Language;
import de.example.backend.entity.ingredient.IngredientEntity;
import de.example.backend.repository.IngredientRepository;
import de.example.backend.repository.querydsl.IngredientQueries;
import de.example.backend.service.mapper.IngredientMapper;
import de.example.backend.service.util.StandingDataCsvReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientQueries ingredientQueries;

    private final IngredientMapper ingredientMapper;

    /**
     * Get all standing data ingredients and the user's custom ingredients
     */
    public List<IngredientUiDTO> getAllWithCustomCreated(UUID userId, Language language) {
        List<IngredientEntity> ingredients = ingredientQueries.get(userId);
        return ingredients.stream()
                .map(ingredient ->
                        ingredientMapper.toDTO(ingredient, language))
                .toList();
    }

    public List<IngredientUiDTO> getAllStandingData(Language language) {
        List<IngredientEntity> ingredients = ingredientRepository.findAllByCreatedByNull();
        return ingredients.stream()
                .map(ingredient -> ingredientMapper.toDTO(ingredient, language))
                .toList();
    }

    public IngredientEntity getStandingDataByEnglishName(String englishName) {
        return ingredientQueries.getStandingDataByEnglishName(englishName)
                .orElseThrow();
    }

    public IngredientEntity getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow();
    }

    public Collection<IngredientEntity> createAll(List<IngredientEntity> ingredients) {
        return ingredientRepository.persistAll(ingredients);
    }

    public int createStandingDataIfMissing(Resource ingredientsCsv) {
        // TODO LGE: Remove and reinsert standing data
        Set<String> existingIngredientStandingDataEnglishNames = getAllStandingData(Language.EN).stream()
                .map(IngredientUiDTO::getName)
                .collect(Collectors.toSet());

        List<IngredientEntity> ingredients = StandingDataCsvReader.readRows(ingredientsCsv, 3).stream()
                .map(this::toStandingDataIngredient)
                .filter(toCreateIngredient ->
                        !existingIngredientStandingDataEnglishNames.contains(toCreateIngredient.getName(Language.EN)))
                .toList();
        createAll(ingredients);
        return ingredients.size();
    }

    private IngredientEntity toStandingDataIngredient(String[] columns) {
        IngredientEntity ingredient = new IngredientEntity();
        ingredient.putTranslation(Language.EN, columns[0]);
        ingredient.putTranslation(Language.DE, columns[1]);
        ingredient.setType(IngredientType.valueOf(columns[2]));
        return ingredient;
    }
}
