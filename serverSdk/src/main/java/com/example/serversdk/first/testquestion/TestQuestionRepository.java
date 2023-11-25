package com.example.serversdk.first.testquestion;

import com.example.serversdk.first.questionAnswer.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    @Query("SELECT tq.question FROM TestQuestion tq WHERE tq.test.Id = :testId")
    List<Question> findQuestionsByTestId(@Param("testId") Long testId);
}
