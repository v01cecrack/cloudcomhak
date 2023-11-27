package com.example.serversdk.common.test;

import com.example.serversdk.common.discipline.DisciplineRepository;
import com.example.serversdk.common.dtos.AnswerDto;
import com.example.serversdk.common.exception.ConflictException;
import com.example.serversdk.common.questionAnswer.Answer;
import com.example.serversdk.common.questionAnswer.AnswerRepository;
import com.example.serversdk.common.questionAnswer.Question;
import com.example.serversdk.common.questionAnswer.QuestionRepository;
import com.example.serversdk.common.testquestion.TestQuestion;
import com.example.serversdk.common.testquestion.TestQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final DisciplineRepository disciplineRepository;

    public List<Test> getTests(String disciplineName) {
        return testRepository.findTestsByDisciplineName(disciplineName);
    }

    public void updateTest(long id, Test test) {
        test.setId(id);
        testRepository.save(test);
        log.info("Тест обновлен");
    }

    @Transactional
    public void createNewTest(TestRequest testRequest, String disciplineName) {
        Test test = testRequest.getTest();
        if (testRepository.findByTestName(test.getTestName()).isPresent()) {
            throw new ConflictException("Такое название уже есть");
        }
        test.setDiscipline(disciplineRepository.findByName(disciplineName).get());
        List<Question> questions = testRequest.getQuestions();
        Map<Long, Question> questionMap = new HashMap<>();

        for (Question question : questions) {
            questionMap.put(question.getId(), question);
        }
        List<AnswerDto> answerDtos = testRequest.getAnswers();
        answerDtos.forEach(answer -> answer.setCorrect(answer.getCorrect() != null ? answer.getCorrect() : false));
        List<Answer> answers = new ArrayList<>();
        for (AnswerDto answerDto : answerDtos) {
            answers.add(Answer.builder()
                    .id(answerDto.getId())
                    .answer(answerDto.getAnswer())
                    .question(questionMap.get(answerDto.getQuestionId()))
                    .correct(answerDto.getCorrect())
                    .build());
        }
        testRepository.save(test);
        questionRepository.saveAll(questions);
        answerRepository.saveAll(answers);
        testQuestionRepository.saveAll(questions.stream()
                .map(question -> TestQuestion.builder().question(question).test(test).build())
                .collect(Collectors.toList()));
        log.info("Создан тест с названием {}", test.getTestName());
    }

    public TestDto getTestClaims(String testName) {
        Long testId = testRepository.findByTestName(testName).get().getId();
        List<Question> questions = questionRepository.findAllById(testId);
        List<Long> questionIds = questions.stream().map(Question::getId).collect(Collectors.toList());
        List<Answer> answers = answerRepository.findAllByQuestion_IdIn(questionIds);
        log.info("Отправлено содержимое теста c id {}", testId);
        return TestDto.builder()
                .questions(questions)
                .answers(answers)
                .build();
    }
}
