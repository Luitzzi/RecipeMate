package de.example.backend.repository;

import de.example.backend.entity.ExampleBE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExampleRepository extends JpaRepository<ExampleBE, UUID> {
}
