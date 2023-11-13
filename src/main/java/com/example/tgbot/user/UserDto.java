package com.example.tgbot.user;

import com.example.tgbot.group.Group;
import com.example.tgbot.university.University;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Scope(value = "prototype")
public class UserDto {
    long chatId;
    String name;
    String surname;
    String fatherName;
    University university;
    Group group;
    @Getter
    String state;

    public void setStateZero() {
        state = "ZERO";
    }
    public void setStateStart() {
        state = "START";
    }

    public void setStateName() {
        state = "NAME";
    }
    public void setStateSurname() {
        state = "SURNAME";
    }

    public void setStateFatherName() {
        state = "FATHERNAME";
    }

    public void setStateUniversity() {
        state = "UNIVERSITY";
    }
    public void setStateGroup() {
        state = "GROUP";
    }

    public void setStateTest() {
        state = "TEST";
    }

    public void setStateCheck() {
        state = "CHECK";
    }

    public void setStateDiscipline() {
        state = "DISCIPLINE";
    }

    public void setStateStatistics() {
        state = "STATISTICS";
    }

}
