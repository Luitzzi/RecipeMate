package de.example.backend.repository;

import de.example.backend.entity.UserEntity;
import de.example.backend.entity.shoppinglist.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long>, PersistFragment<ShoppingListEntity> {

    Optional<ShoppingListEntity> findByOwner(UserEntity owner);

}
