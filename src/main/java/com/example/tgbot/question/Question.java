package com.example.tgbot.question;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@Entity
@Table(name = "questions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Scope(value = "prototype")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "question_text")
    String questionText;
    @Column(name = "correct_answer")
    String questionAnswer;
    @Column(name = "incorrect_answer1")
    String incorrectAnswer1;
    @Column(name = "incorrect_answer2")
    String incorrectAnswer2;
    @Column(name = "incorrect_answer3")
    String incorrectAnswer3;
    @Column(name = "incorrect_answer4")
    String incorrectAnswer4;

}
