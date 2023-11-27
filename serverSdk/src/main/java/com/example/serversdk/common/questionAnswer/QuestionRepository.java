package com.example.serversdk.common.questionAnswer;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Scope(value = "prototype")
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findByQuestionText(String text);

    List<Question> findAllById(Long id);

    List<Question> findAllByIdIn(List<Long> ids);

}
