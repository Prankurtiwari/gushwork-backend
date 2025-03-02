package com.gushwork.SlotMachine.model;

import java.util.UUID;


public class Session {
    private final String sessionId;

    private int credits = 10;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getCredits() {
        return credits;
    }

}
