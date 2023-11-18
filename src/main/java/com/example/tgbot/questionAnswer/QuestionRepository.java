package com.example.tgbot.questionAnswer;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

@Scope(value = "prototype")
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
