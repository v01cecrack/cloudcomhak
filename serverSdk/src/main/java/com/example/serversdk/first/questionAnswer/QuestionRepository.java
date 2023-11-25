package com.example.serversdk.first.questionAnswer;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Scope(value = "prototype")
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findByQuestionText(String text);

    List<Question> findAllById(Long id);
}
