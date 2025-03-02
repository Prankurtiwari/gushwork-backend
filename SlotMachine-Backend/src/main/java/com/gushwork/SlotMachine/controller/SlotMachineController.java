package com.gushwork.SlotMachine.controller;

import com.gushwork.SlotMachine.exceptions.CreditNotEnoughException;
import com.gushwork.SlotMachine.exceptions.InvalidUserException;
import com.gushwork.SlotMachine.exceptions.UserAlreadyExistException;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.User;
import com.gushwork.SlotMachine.service.SlotMachineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/slot-machine")
public class SlotMachineController {
    private final SlotMachineService slotMachineService;

    public SlotMachineController(SlotMachineService slotMachineService) {
        this.slotMachineService = slotMachineService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(@Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok(slotMachineService.createSession(user));
        }catch (UserAlreadyExistException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/roll/{sessionId}")
    public ResponseEntity<?> roll(@PathVariable String sessionId) {
        try {
            return ResponseEntity.ok(slotMachineService.roll(sessionId));
        } catch (InvalidUserException | CreditNotEnoughException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/cash-out/{sessionId}")
    public ResponseEntity<?>  cashOut(@PathVariable String sessionId) {
        try {
            return ResponseEntity.ok(slotMachineService.cashOut(sessionId));
        } catch (InvalidUserException | CreditNotEnoughException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
