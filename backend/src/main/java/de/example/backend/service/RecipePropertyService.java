package de.example.backend.service;

import de.example.backend.entity.recipe.RecipePropertyEntity;
import de.example.backend.repository.RecipePropertyRepository;
import de.example.backend.service.util.StandingDataCsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipePropertyService {

    private final RecipePropertyRepository recipePropertyRepository;

    public List<RecipePropertyEntity> getByIds(List<Long> ids) {
        return recipePropertyRepository.findAllById(ids);
    }

    public RecipePropertyEntity getByName(String name) {
        return recipePropertyRepository.findByName(name)
                .orElseThrow();
    }

    public int createStandingDataIfMissing(Resource propertiesCsv) {
        Set<String> existingNames = recipePropertyRepository.findAll().stream()
                .map(RecipePropertyEntity::getName)
                .collect(Collectors.toSet());

        List<RecipePropertyEntity> properties = StandingDataCsvReader.readRows(propertiesCsv, 1).stream()
                .map(columns -> columns[0])
                .filter(name -> !existingNames.contains(name))
                .map(name -> new RecipePropertyEntity(null, name))
                .toList();
        recipePropertyRepository.persistAll(properties);
        return properties.size();
    }
}
