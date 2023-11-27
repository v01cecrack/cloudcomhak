package com.example.serversdk.common.test;

import com.example.serversdk.common.dtos.AnswerDto;
import com.example.serversdk.common.questionAnswer.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestRequest {
    private Test test;
    private List<Question> questions;
    private List<AnswerDto> answers;
}
