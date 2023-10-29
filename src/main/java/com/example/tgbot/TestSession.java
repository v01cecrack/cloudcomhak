package com.example.tgbot;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Scope(value = "prototype")
public class TestSession {
    public Integer currentQuestion = 0;
    public List<String> userAnswers = new ArrayList<>();
}
