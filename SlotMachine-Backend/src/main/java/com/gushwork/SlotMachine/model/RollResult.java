package com.gushwork.SlotMachine.model;


import java.util.List;


public class RollResult{
    private final List<Symbol> symbols;
    private final boolean isWinner;
    private final int reward;
    private final int balance;

    public RollResult(List<Symbol> symbols, boolean isWinner, int reward, int balance) {
        this.symbols = symbols;
        this.isWinner = isWinner;
        this.reward = reward;
        this.balance = balance;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public int getReward() {
        return reward;
    }

    public int getBalance() {
        return balance;
    }
}
