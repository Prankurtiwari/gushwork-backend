package com.gushwork.SlotMachine.service;

import com.gushwork.SlotMachine.exceptions.CreditNotEnoughException;
import com.gushwork.SlotMachine.exceptions.InvalidUserException;
import com.gushwork.SlotMachine.model.RollResult;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.Symbol;
import com.gushwork.SlotMachine.model.User;
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

    public Session createSession(User user) {
        logger.info("Creating a new game session for user name: " + user.getName() + " email id : " + user.getEmail());
        return  sessionRepository.createSession(user);
    }

    public RollResult roll(String sessionId) {
        Session session = sessionRepository.getSession(sessionId);

        if (session == null) {
            throw new InvalidUserException(INVALID_USER);
        }
        synchronized (session) {
            if (session.getCredit() < 1) {
                throw new CreditNotEnoughException(CREDIT_NOT_ENOUGH);
            }
            logger.info("Rolling the slot for user: {}", session.getUser().getEmail());
            session.setCredit(session.getCredit() - oneRollCredit);
            logger.info("User {} rolled, 1 credit deducted. Remaining credits: {}", session.getUser().getEmail(), session.getCredit());
            List<Symbol> symbols = generateRandomSymbols();
            boolean isWinner = symbols.stream().distinct().count() == 1;
            int reward = isWinner ? symbols.get(0).getReward() : 0;
            if (isWinner && rollStrategy.shouldRoll(session.getCredit())) {
                logger.info("Re-rolling due to server pivoted logic.");
                symbols = generateRandomSymbols();
                isWinner = false;
                reward = 0;
            }

            if (isWinner) {
                logger.info("User {} won! Reward: {} credits. ", session.getUser().getEmail(), reward);
                session.setCredit(session.getCredit() + reward);
            }
            logger.info("User {} final {} credits. ", session.getUser().getEmail(), session.getCredit());
            return new RollResult(symbols, isWinner, reward, session.getCredit());
        }
    }

    public int cashOut(String sessionId) {
        Session session = sessionRepository.getSession(sessionId);
        if (session == null) {
            throw new InvalidUserException(INVALID_USER);
        }
        if (session.getCredit() <= 0) {
            throw new CreditNotEnoughException(CREDIT_NOT_ENOUGH);
        }
        synchronized (session) {
            int creditsToCashOut = session.getCredit();
            logger.info("Cashing out {} credits for user : {}", creditsToCashOut, session.getUser().getEmail());
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
