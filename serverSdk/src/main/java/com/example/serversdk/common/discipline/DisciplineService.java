package com.example.serversdk.common.discipline;

import com.example.serversdk.common.disciplinegroup.DisciplineGroup;
import com.example.serversdk.common.disciplinegroup.DisciplineGroupRepository;
import com.example.serversdk.common.exception.ConflictException;
import com.example.serversdk.common.group.Group;
import com.example.serversdk.common.questionAnswer.AnswerRepository;
import com.example.serversdk.common.questionAnswer.QuestionRepository;
import com.example.serversdk.common.test.Test;
import com.example.serversdk.common.test.TestRepository;
import com.example.serversdk.common.testquestion.TestQuestionRepository;
import com.example.serversdk.common.result.ResultRepository;
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
    private final DisciplineGroupRepository disciplineGroupRepository;
    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ResultRepository resultRepository;


    public List<Discipline> getDisciplines() {
        log.info("Получен запрос на получение всех дисциплин");
        return disciplineRepository.findAll();
    }

    @Transactional
    public void postDiscipline(DisciplineDto discipline) {
        log.info("Получен запрос на добавление дисциплины {}", discipline.getDiscipline().getName());
        if (disciplineRepository.findByName(discipline.discipline.getName()).isPresent()) {
            throw new ConflictException("Такая дисциплина уже существует");
        }
        Discipline savedDiscipline = disciplineRepository.save(discipline.getDiscipline());

        List<Group> groups = discipline.getGroups();

        if (!groups.isEmpty()) {
            List<DisciplineGroup> disciplineGroups = groups.stream()
                    .map(group -> DisciplineGroup.builder().discipline(savedDiscipline).group(group).build())
                    .collect(Collectors.toList());

            disciplineGroupRepository.saveAll(disciplineGroups);
        }
    }

    @Transactional
    public void deleteDiscipline(Long id) {
        log.info("Получен запрос на удаление дисциплины");
        disciplineGroupRepository.deleteAllByDiscipline_Id(id);
        List<Test> tests = testRepository.findAllByDiscipline_Id(id);
        testQuestionRepository.deleteAllByTestIn(tests);
        testRepository.deleteAllById(tests.stream().map(Test::getId).collect(Collectors.toList()));
        disciplineRepository.deleteById(id);
    }

    public void updateDiscipline(Long id, Discipline discipline) {
        Discipline existingDiscipline = disciplineRepository.findById(id).get();
        existingDiscipline.setName(discipline.getName());
        disciplineRepository.save(existingDiscipline);
        log.info("Обновлена дисциплина");
    }
}
