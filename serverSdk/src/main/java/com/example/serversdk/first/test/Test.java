package com.example.serversdk.first.test;

import com.example.serversdk.first.discipline.Discipline;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    Long testId;

    @Column(name = "test_name")
    String testName;

    @ManyToOne
    @JoinColumn(name = "discipline_name")
    Discipline discipline;
}
