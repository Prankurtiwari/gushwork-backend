package com.gushwork.SlotMachine.service;

import com.gushwork.SlotMachine.exceptions.CreditNotEnoughException;
import com.gushwork.SlotMachine.exceptions.InvalidUserException;
import com.gushwork.SlotMachine.model.RollResult;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.Symbol;
import com.gushwork.SlotMachine.respository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.gushwork.SlotMachine.constants.MachineConstant.CREDIT_NOT_ENOUGH;
import static com.gushwork.SlotMachine.constants.MachineConstant.INVALID_USER;

@Service
public class SlotMachineService {

    private static final Logger logger = LoggerFactory.getLogger(SlotMachineService.class);
    private final SessionRepository sessionRepository;
    private final RollStrategy rollStrategy;
    private final Random random= new Random();

    @Value("${app.one-roll-credit}")
    private int oneRollCredit;

    @Value("${app.random-response-times}")
    private int noOfRandomResponse;

    public SlotMachineService(SessionRepository sessionRepository, RollStrategy rollStrategy) {
        this.sessionRepository = sessionRepository;
        this.rollStrategy = rollStrategy;
    }

    public Session createSession() {
        Session session = sessionRepository.createSession();
        logger.info("Creating a new game session for session id: " + session.getSessionId());
        return session;
    }

    public RollResult roll(String sessionId) {
        Session session = sessionRepository.getSession(sessionId);

        if (session == null) {
            throw new InvalidUserException(INVALID_USER);
        }
        synchronized (session) {
            if (session.getCredits() < 1) {
                throw new CreditNotEnoughException(CREDIT_NOT_ENOUGH);
            }
            logger.info("Rolling the slot for session id: {}", sessionId);
            session.setCredits(session.getCredits() - oneRollCredit);
            logger.info("Session {} rolled, 1 credit deducted. Remaining credits: {}", sessionId, session.getCredits());
            List<Symbol> symbols = generateRandomSymbols();
            boolean isWinner = symbols.stream().distinct().count() == 1;
            int reward = isWinner ? symbols.get(0).getReward() : 0;
            if (isWinner && rollStrategy.shouldRoll(session.getCredits())) {
                logger.info("Re-rolling due to server pivoted logic.");
                symbols = generateRandomSymbols();
                isWinner = false;
                reward = 0;
            }

            if (isWinner) {
                logger.info("Session {} won! Reward: {} credits. ",sessionId, reward);
                session.setCredits(session.getCredits() + reward);
            }
            logger.info("Session {} final {} credits. ", sessionId, session.getCredits());
            return new RollResult(symbols, isWinner, reward, session.getCredits());
        }
    }

    public int cashOut(String sessionId) {
        Session session = sessionRepository.getSession(sessionId);
        if (session == null) {
            throw new InvalidUserException(INVALID_USER);
        }
        if (session.getCredits() <= 0) {
            throw new CreditNotEnoughException(CREDIT_NOT_ENOUGH);
        }
        synchronized (session) {
            int creditsToCashOut = session.getCredits();
            logger.info("Cashing out {} credits for session : {}", creditsToCashOut, sessionId);
            sessionRepository.deleteSession(sessionId);
            return creditsToCashOut;
        }
    }

    public List<Symbol> generateRandomSymbols() {
        Symbol[] values = Symbol.values();
        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < noOfRandomResponse; i++) {
            symbols.add(values[random.nextInt(values.length)]);
        }
        return symbols;
    }




}
