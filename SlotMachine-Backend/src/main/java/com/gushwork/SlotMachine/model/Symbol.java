package com.gushwork.SlotMachine.model;

public enum Symbol {
    CHERRY(10),
    LEMON(20),
    ORANGE(30),
    WATERMELON(40);

    private final int reward;

    Symbol(int reward) {
        this.reward = reward;
    }

    public int getReward() {
        return reward;
    }
}
