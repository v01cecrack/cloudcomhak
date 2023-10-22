package com.example.tgbot;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class SimpleTest {
    List<String> questions = List.of(
            "Какого цвета синяя лошадь?",
            "Сколько пальцев на руке?",
            "Я самый не гей но я?",
            "Сто минус девяносто девять"
    );
    List<String> correctAnswers = List.of(
            "синего",
            "пять",
            "Сергей",
            "один"
    );
}
