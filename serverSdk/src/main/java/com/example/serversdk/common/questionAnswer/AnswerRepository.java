package com.example.serversdk.common.questionAnswer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {


    List<Answer> findAllByQuestion_IdIn(List<Long> questionIds);

}
