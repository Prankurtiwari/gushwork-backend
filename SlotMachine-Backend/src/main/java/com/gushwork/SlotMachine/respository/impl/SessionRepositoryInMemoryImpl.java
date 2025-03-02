package com.gushwork.SlotMachine.respository.impl;

import com.gushwork.SlotMachine.exceptions.UserAlreadyExistException;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.User;
import com.gushwork.SlotMachine.respository.SessionRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.gushwork.SlotMachine.constants.MachineConstant.KEY_SEPARATOR;
import static com.gushwork.SlotMachine.constants.MachineConstant.USER_ALREADY_EXIST;

@Repository
@Primary
public class SessionRepositoryInMemoryImpl implements SessionRepository {

    Map<String, Session> sessions = new ConcurrentHashMap<>();
    @Override
    public Session createSession(User user) {
       Session session = new Session(user);
       if (sessions.keySet().stream()
               .map(s -> s.substring(s.indexOf(KEY_SEPARATOR) + 1))
               .toList().stream().anyMatch(s -> s.equals(user.getEmail()))) {
           throw new UserAlreadyExistException(USER_ALREADY_EXIST + user.getEmail());
       };
       sessions.put( session.getSessionId(), session);

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
