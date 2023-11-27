package com.example.serversdk.common.discipline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public void postDiscipline(@RequestBody DisciplineDto discipline) {
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
