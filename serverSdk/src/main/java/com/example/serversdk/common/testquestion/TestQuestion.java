package com.example.serversdk.common.testquestion;

import com.example.serversdk.common.test.Test;
import com.example.serversdk.common.questionAnswer.Question;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@Entity
@Table(name = "Test_Questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Scope(value = "prototype")
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "test_id")
    Test test;
    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;
}
