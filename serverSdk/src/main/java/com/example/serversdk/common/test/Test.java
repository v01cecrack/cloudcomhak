package com.example.serversdk.common.test;

import com.example.serversdk.common.discipline.Discipline;
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
    @Column(name = "test_id")
    Long Id;

    @Column(name = "test_name")
    String testName;

    @ManyToOne
    @JoinColumn(name = "discipline_name", referencedColumnName = "name")
    Discipline discipline;
}
