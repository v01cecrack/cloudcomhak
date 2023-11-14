package com.example.tgbot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionData {
    private String questionText;
    private String correctAnswer;
    private List<String> incorrectAnswers;
}
