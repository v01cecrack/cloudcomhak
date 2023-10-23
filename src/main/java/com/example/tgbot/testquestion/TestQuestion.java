package com.example.tgbot.testquestion;

import com.example.tgbot.question.Question;
import com.example.tgbot.test.Test;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "test_questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
