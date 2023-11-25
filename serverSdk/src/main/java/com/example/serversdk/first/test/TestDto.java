package com.example.serversdk.first.test;

import com.example.serversdk.first.questionAnswer.Answer;
import com.example.serversdk.first.questionAnswer.Question;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestDto {
    List<Question> questions;
    List<Answer> answers;
}
