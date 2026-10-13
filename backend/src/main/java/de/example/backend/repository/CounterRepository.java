package de.example.backend.repository;

import de.example.backend.entity.CounterBE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CounterRepository extends JpaRepository<CounterBE, UUID> {
}
