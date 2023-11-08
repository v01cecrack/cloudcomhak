package com.example.tgbot.disciplinegroup;

import com.example.tgbot.discipline.Discipline;
import com.example.tgbot.group.Group;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@Entity
@Table(name = "Discipline_Groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Scope(value = "prototype")
public class DisciplineGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discipline_group_id")
    Long id;
    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;
    @ManyToOne
    @JoinColumn(name = "group_name", referencedColumnName = "name")
    private Group group;
}
