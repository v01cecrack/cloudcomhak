package com.example.tgbot.discipline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/disciplines")
public class DisciplineController {
    private final DisciplineService disciplineService;

    @GetMapping()
    public List<Discipline> getDisciplines() {
        return disciplineService.getDisciplines();
    }

    @PostMapping()
    public void postDiscipline(@RequestBody Discipline discipline) {
        disciplineService.postDiscipline(discipline);
    }

    @DeleteMapping("/{id}")
    public void deleteDiscipline(@PathVariable Long id) {
        disciplineService.deleteDiscipline(id);
    }

    @PatchMapping("/{id}")
    public void updateDiscipline(@PathVariable Long id, @RequestBody Discipline discipline) {
        disciplineService.updateDiscipline(id, discipline);
    }


}
