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
        answerDtos.forEach(answer -> answer.setCorrect(answer.getCorrect() != null && answer.getCorrect()));

        List<Answer> answers = new ArrayList<>();
        for (AnswerDto answerDto : answerDtos) {
            answers.add(Answer.builder()
                    .answer(answerDto.getAnswer())
                    .question(questionMap.get(answerDto.getQuestionId()))
                    .correct(answerDto.getCorrect())
                    .build());
        }

        // Сначала сохраняем вопросы
        questionRepository.saveAll(questions);

        // Затем сохраняем ответы
        answerRepository.saveAll(answers);

        // Связываем тест с вопросами
        List<TestQuestion> testQuestions = questions.stream()
                .map(question -> TestQuestion.builder().question(question).test(test).build())
                .collect(Collectors.toList());

        testRepository.save(test);
        testQuestionRepository.saveAll(testQuestions);

        log.info("Создан тест с названием {}", test.getTestName());
    }


    public TestRequest getTestClaims(Long testId) {
        Test test = testRepository.findById(testId).get();
        List<TestQuestion> testQuestions = testQuestionRepository.findAllByTest(test);

        List<Long> questionIds = testQuestions.stream()
                .map(testQuestion -> testQuestion.getQuestion().getId())
                .collect(Collectors.toList());

        List<Question> questions = questionRepository.findAllById(questionIds);

        List<Answer> answers = answerRepository.findAllByQuestion_IdIn(questionIds);
        List<AnswerDto> answerDtos = answers.stream().map(TestService::mapToAnswerDto).collect(Collectors.toList());

        log.info("Отправлено содержимое теста c id {}", testId);

        return TestRequest.builder()
                .test(test)
                .questions(questions)
                .answers(answerDtos)
                .build();
    }

    private static AnswerDto mapToAnswerDto(Answer answer) {
        return AnswerDto.builder()
                .id(answer.getId())
                .answer(answer.getAnswer())
                .questionId(answer.getQuestion().getId())
                .correct(answer.getCorrect())
                .build();
    }

}
