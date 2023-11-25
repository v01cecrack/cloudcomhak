package com.example.serversdk.first.test;

import com.example.serversdk.first.questionAnswer.Answer;
import com.example.serversdk.first.questionAnswer.AnswerRepository;
import com.example.serversdk.first.questionAnswer.Question;
import com.example.serversdk.first.questionAnswer.QuestionRepository;
import com.example.serversdk.first.testquestion.TestQuestion;
import com.example.serversdk.first.testquestion.TestQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TestQuestionRepository testQuestionRepository;

    public List<Test> getTests(String disciplineName) {
        return testRepository.findTestsByDisciplineName(disciplineName);
    }

    public void updateTest(long id, Test test) {
        test.setTestId(id);
        testRepository.save(test);
        log.info("Тест обновлен");
    }

    @Transactional
    public void createNewTest (TestRequest testRequest) {
        Test test = testRequest.getTest();
        List<Question> questions = testRequest.getQuestions();
        List<Answer> answers = testRequest.getAnswers();
        testRepository.save(test);
        questionRepository.saveAll(questions);
        answerRepository.saveAll(answers);
        log.info("Создан тест с названием {}", test.getTestName());
    }
}
