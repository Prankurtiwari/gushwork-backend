package com.gushwork.SlotMachine.service;

public interface RollStrategy {
    boolean shouldRoll(int credits);
}
