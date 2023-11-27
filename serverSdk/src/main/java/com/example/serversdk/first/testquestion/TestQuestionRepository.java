package com.example.serversdk.first.testquestion;

import com.example.serversdk.first.questionAnswer.Question;
import com.example.serversdk.first.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {

    void deleteAllByTestIn(List<Test> tests);
}
