package com.gushwork.SlotMachine.respository;

import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.User;


public interface SessionRepository {
    Session createSession(User user);
    Session getSession(String sessionID);

    void deleteSession(String sessionId);
}
