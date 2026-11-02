package de.example.backend.repository;

import de.example.backend.entity.mealprep.MealPlanDayEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanDayRepository extends JpaRepository<MealPlanDayEntity, Long>, PersistFragment<MealPlanDayEntity> {

    List<MealPlanDayEntity> findAllByOwnerAndDateBetweenOrderByDate(UserEntity owner, LocalDate from, LocalDate to);

    Optional<MealPlanDayEntity> findByOwnerAndDate(UserEntity owner, LocalDate date);

}
