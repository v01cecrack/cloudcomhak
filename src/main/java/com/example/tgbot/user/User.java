package com.example.tgbot.user;

import com.example.tgbot.group.Group;
import com.example.tgbot.university.University;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Scope(value = "prototype")
public class User {
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
