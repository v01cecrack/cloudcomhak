package com.example.serversdk.first.questionAnswer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllByQuestion_IdAndCorrectIsFalse(Long questionId);
    Answer findByQuestion_IdAndCorrectIsTrue(Long questionId);
}
