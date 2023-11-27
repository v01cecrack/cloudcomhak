package com.example.serversdk.common.testquestion;

import com.example.serversdk.common.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

    void deleteAllByTestIn(List<Test> tests);

    List<TestQuestion> findAllByTest(Test test);
}
