package com.gushwork.SlotMachine.service.strategy;

import com.gushwork.SlotMachine.service.RollStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class DefaultRollingStrategy implements RollStrategy {

    @Value("${app.no-cheat-credit}")
    private int noCheatCredit;
    @Override
    public boolean shouldRoll(int credits) {
        if (credits < noCheatCredit) {
            return false;
        }
        double chance = credits <= 60 ? 0.3 : 0.6;

        return Math.random() < chance;
    }
}
