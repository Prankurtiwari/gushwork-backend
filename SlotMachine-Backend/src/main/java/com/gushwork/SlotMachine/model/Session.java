package com.gushwork.SlotMachine.model;

import java.util.UUID;

import static com.gushwork.SlotMachine.constants.MachineConstant.KEY_SEPARATOR;


public class Session {
    private final String sessionId;
    private final User user;

    private int credit = 10;

    public Session(User user) {
        this.user = user;
        this.sessionId = UUID.randomUUID() + KEY_SEPARATOR + user.getEmail();;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getCredit() {
        return credit;
    }

    public User getUser() {
        return user;
    }
}
