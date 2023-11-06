package com.example.tgbot.discipline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DisciplineController {
    private final DisciplineService disciplineService;

    @GetMapping("/disciplines")
    public List<Discipline> getDisciplines() {
        return disciplineService.getDisciplines();
    }

    @PostMapping("/disciplines")
    public void postDiscipline(@RequestBody Discipline discipline) {
        disciplineService.postDiscipline(discipline);
    }

}
