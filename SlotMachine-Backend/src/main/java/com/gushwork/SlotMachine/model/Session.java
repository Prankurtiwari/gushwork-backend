package com.gushwork.SlotMachine.model;

import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;


public class Session {
    private final String sessionId;
    private final User user;

    @Value("${app.default-credit}")
    private int credit;

    public Session(User user) {
        this.user = user;
        this.sessionId = UUID.randomUUID().toString();
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
