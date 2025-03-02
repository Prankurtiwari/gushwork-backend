package com.gushwork.SlotMachine.respository;

import com.gushwork.SlotMachine.model.Session;


public interface SessionRepository {
    Session createSession();
    Session getSession(String sessionID);

    void deleteSession(String sessionId);
}
