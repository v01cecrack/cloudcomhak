package com.example.tgbot.test;

import com.example.tgbot.discipline.Discipline;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@Entity
@Table(name = "Tests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Test {
    @Id
    @Column(name = "test_id")
    Long testId;

    @Column(name = "test_name")
    String testName;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    Discipline discipline;
}
