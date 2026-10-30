package de.example.backend.repository;

import de.example.backend.entity.shoppinglist.ShoppingListEntity;
import de.example.backend.entity.user.UserEntity;
import de.example.backend.repository.fragments.PersistFragment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, Long>, PersistFragment<ShoppingListEntity> {

    Optional<ShoppingListEntity> findByOwner(UserEntity owner);

}
