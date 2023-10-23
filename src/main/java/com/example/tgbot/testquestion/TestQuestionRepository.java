package com.example.tgbot.testquestion;

import com.example.tgbot.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    @Query("SELECT tq.question FROM TestQuestion tq WHERE tq.test.testId = :testId")
    List<Question> findQuestionsByTestId(@Param("testId") Long testId);
}
