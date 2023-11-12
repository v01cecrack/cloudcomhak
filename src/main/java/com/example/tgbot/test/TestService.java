package com.example.tgbot.test;

import com.example.tgbot.question.Question;
import com.example.tgbot.question.QuestionRepository;
import com.example.tgbot.testquestion.TestQuestion;
import com.example.tgbot.testquestion.TestQuestionRepository;
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
    private final TestQuestionRepository testQuestionRepository;

    public List<Test> getTests(String disciplineName) {
        return testRepository.findTestsByDisciplineName(disciplineName);
    }

    @Transactional
    public void postTest(String disciplineName, TestRequest testRequest) {
        Test test = testRequest.getTest();
        List<Question> questions = testRequest.getQuestions();
        test.setDisciplineName(disciplineName);
        testRepository.save(test);
        questionRepository.saveAll(questions);

        List<TestQuestion> testQuestions = new ArrayList<>();
        for (Question question : questions) {
            TestQuestion testQuestion = new TestQuestion();
            testQuestion.setTest(test);
            testQuestion.setQuestion(question);
            testQuestions.add(testQuestion);
        }
        testQuestionRepository.saveAll(testQuestions);
        log.info("Создан тест с названием {}", test.getTestName());
    }
}
