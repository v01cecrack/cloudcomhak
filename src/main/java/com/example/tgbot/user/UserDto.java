package com.example.tgbot.user;

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
    String group;
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
    public void setStateGroup() {
        state = "GROUP";
    }

    public void setStateTest() {
        state = "TEST";
    }

    public void setStateCheck() {
        state = "CHECK";
    }

}
