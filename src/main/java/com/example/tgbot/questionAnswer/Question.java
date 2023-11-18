package com.example.tgbot.questionAnswer;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@Entity
@Table(name = "Questions")
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
}
