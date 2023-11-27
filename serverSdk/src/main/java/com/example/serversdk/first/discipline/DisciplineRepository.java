package com.example.serversdk.first.discipline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    Discipline findByName(String name);

}
