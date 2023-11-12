package com.example.tgbot.test;

import com.example.tgbot.question.Question;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TestRequest {
    private Test test;
    private List<Question> questions;
}
