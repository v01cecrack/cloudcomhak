package com.example.serversdk.first.discipline;

import com.example.serversdk.first.disciplinegroup.DisciplineGroup;
import com.example.serversdk.first.disciplinegroup.DisciplineGroupRepository;
import com.example.serversdk.first.group.Group;
import com.example.serversdk.first.group.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;
    private final GroupRepository groupRepository;
    private final DisciplineGroupRepository disciplineGroupRepository;

    public List<Discipline> getDisciplines() {
        log.info("Получен запрос на получение всех дисциплин");
        return disciplineRepository.findAll();
    }

    @Transactional
    public void postDiscipline(DisciplineDto discipline) {
        log.info("Получен запрос на добавление дисциплины {}", discipline.getDiscipline().getName());
        Discipline savedDiscipline = disciplineRepository.save(discipline.getDiscipline());

        List<Group> groups = discipline.getGroups();

        if (!groups.isEmpty()) {
            List<DisciplineGroup> disciplineGroups = groups.stream()
                    .map(group -> DisciplineGroup.builder().discipline(savedDiscipline).group(group).build())
                    .collect(Collectors.toList());

            disciplineGroupRepository.saveAll(disciplineGroups);
        }
    }

    public void deleteDiscipline(Long id) {
        log.info("Получен запрос на удаление дисциплины");
        disciplineRepository.deleteById(id);
    }

    public void updateDiscipline(Long id, Discipline discipline) {
        discipline.setId(id);
        disciplineRepository.save(discipline);
        log.info("Обновлена дисциплина");
    }
}
