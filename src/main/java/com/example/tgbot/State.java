package com.example.tgbot;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class State {
    private String stateFlag;

    public void setStateZero() {
        stateFlag = "ZERO";
    }
    public void setStateStart() {
        stateFlag = "START";
    }

    public void setStateName() {
        stateFlag = "NAME";
    }
    public void setStateSurname() {
        stateFlag = "SURNAME";
    }
    public void setStateGroup() {
        stateFlag = "GROUP";
    }

    public void setStateTest() {
        stateFlag = "TEST";
    }

    public void setStateCheck() {
        stateFlag = "CHECK";
    }

    public String getState() {
        return stateFlag;
    }
}
