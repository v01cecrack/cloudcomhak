package com.example.tgbot.discipline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;

    public List<Discipline> getDisciplines() {
        log.info("Получен запрос на получение всех дисциплин");
        return disciplineRepository.findAll();
    }

    public void postDiscipline(Discipline discipline) {
        log.info("Получен запрос на добавление дисциплины {}", discipline);
        disciplineRepository.save(discipline);
    }

    public void deleteDiscipline(Long id) {
        log.info("Получен запрос на удаление дисциплины");
        disciplineRepository.deleteById(id);
    }
}
