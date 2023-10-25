package com.example.tgbot.result;

import com.example.tgbot.question.Question;
import com.example.tgbot.test.Test;
import com.example.tgbot.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "Results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "test_id")
    Test test;
    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;
    @Column(name = "answer")
    String answer;
}
