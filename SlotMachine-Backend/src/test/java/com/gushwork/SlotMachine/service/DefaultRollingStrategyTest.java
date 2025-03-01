package com.gushwork.SlotMachine.service;

import com.gushwork.SlotMachine.service.strategy.DefaultRollingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class DefaultRollingStrategyTest {
    private RollStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new DefaultRollingStrategy();
    }

    @Test
    public void testNoRollBelow40Credits() {
        // For credits below 40, the strategy should always return false.
        assertFalse(strategy.shouldRoll(39));
    }

    @Test
    public void testRollProbabilityBetween40And60Credits() {
        int iterations = 10000;
        int rerollCount = 0;
        for (int i = 0; i < iterations; i++) {
            if (strategy.shouldRoll(50)) {
                rerollCount++;
            }
        }
        double probability = (double) rerollCount / iterations;
        // For credits between 40 and 60, expect roughly 30% roll probability.
        assertTrue(probability >= 0.25 && probability <= 0.35,
                "Expected roll probability ~0.3, but got " + probability);
    }

    @Test
    public void testRollProbabilityAbove60Credits() {
        int iterations = 10000;
        int rerollCount = 0;
        for (int i = 0; i < iterations; i++) {
            if (strategy.shouldRoll(70)) {
                rerollCount++;
            }
        }
        double probability = (double) rerollCount / iterations;
        // For credits above 60, expect roughly 60% roll probability.
        assertTrue(probability >= 0.55 && probability <= 0.65,
                "Expected roll probability ~0.6, but got " + probability);
    }
}
