package com.gushwork.SlotMachine.exceptions;

public class CreditNotEnoughException extends RuntimeException{
    public CreditNotEnoughException(String message) {
        super(message);
    }
}
