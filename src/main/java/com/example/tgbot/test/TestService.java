package com.example.tgbot.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    public List<Test> getTests(String disciplineName) {
        return testRepository.findTestsByDisciplineName(disciplineName);
    }

    public void postTest(String disciplineName, Test test) {
        test.setDisciplineName(disciplineName);
        testRepository.save(test);
    }
}
