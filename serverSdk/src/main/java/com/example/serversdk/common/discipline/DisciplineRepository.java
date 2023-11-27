package com.example.serversdk.common.discipline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    Optional<Discipline> findByName(String name);

}
