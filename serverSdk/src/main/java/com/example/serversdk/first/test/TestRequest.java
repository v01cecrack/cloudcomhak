package com.example.serversdk.first.test;

import com.example.serversdk.first.questionAnswer.Answer;
import com.example.serversdk.first.questionAnswer.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestRequest {
    private Test test;
    private List<Question> questions;
    private List<Answer> answers;
}
