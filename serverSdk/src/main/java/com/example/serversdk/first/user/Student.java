package com.example.serversdk.first.user;

import com.example.serversdk.first.group.Group;
import com.example.serversdk.first.university.University;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Students")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Scope(value = "prototype")
public class Student {
    @Id
    @Column(name = "chat_id")
    Long chatId;
    @Column
    String name;
    @Column
    String surname;
    @Column(name = "father_name")
    String fatherName;
    @ManyToOne
    @JoinColumn(name = "university_name", referencedColumnName = "name")
    University university;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_name", referencedColumnName = "name")
    Group group;
}
