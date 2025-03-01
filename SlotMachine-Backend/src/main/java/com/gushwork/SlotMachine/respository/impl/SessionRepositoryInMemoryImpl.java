package com.gushwork.SlotMachine.respository.impl;

import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.User;
import com.gushwork.SlotMachine.respository.SessionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.gushwork.SlotMachine.constants.MachineConstant.KEY_SEPARATOR;

@Repository
@Primary
public class SessionRepositoryInMemoryImpl implements SessionRepository {

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Override
    public Session createSession(User user) {
       Session session = new Session(user);
       String key = session.getSessionId() + KEY_SEPARATOR + user.getEmail();
       sessions.put(key, session);
       return session;
    }

    @Override
    public Session getSession(String sessionID) {
       return sessions.get(sessionID);
    }

    @Override
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
