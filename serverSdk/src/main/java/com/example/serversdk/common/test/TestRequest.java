package com.example.serversdk.common.test;

import com.example.serversdk.common.dtos.AnswerDto;
import com.example.serversdk.common.questionAnswer.Question;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class TestRequest {
    private Test test;
    private List<Question> questions;
    private List<AnswerDto> answers;
}
