package de.example.backend.service;

import de.example.backend.entity.recipe.RecipeCuisineEntity;
import de.example.backend.repository.RecipeCuisineRepository;
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
public class RecipeCuisineService {

    private final RecipeCuisineRepository recipeCuisineRepository;

    public List<RecipeCuisineEntity> getByIds(List<Long> ids) {
        return recipeCuisineRepository.findAllById(ids);
    }

    public RecipeCuisineEntity getByName(String name) {
        return recipeCuisineRepository.findByName(name)
                .orElseThrow();
    }

    public int createStandingDataIfMissing(Resource cuisinesCsv) {
        Set<String> existingNames = recipeCuisineRepository.findAll().stream()
                .map(RecipeCuisineEntity::getName)
                .collect(Collectors.toSet());

        List<RecipeCuisineEntity> cuisines = StandingDataCsvReader.readRows(cuisinesCsv, 1).stream()
                .map(columns -> columns[0])
                .filter(name -> !existingNames.contains(name))
                .map(this::toStandingDataCuisine)
                .toList();
        recipeCuisineRepository.persistAll(cuisines);
        return cuisines.size();
    }

    private RecipeCuisineEntity toStandingDataCuisine(String name) {
        RecipeCuisineEntity cuisine = new RecipeCuisineEntity();
        cuisine.setName(name);
        return cuisine;
    }

}
