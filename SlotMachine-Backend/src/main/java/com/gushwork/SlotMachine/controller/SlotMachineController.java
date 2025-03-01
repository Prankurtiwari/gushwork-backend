package com.gushwork.SlotMachine.controller;

import com.gushwork.SlotMachine.model.RollResult;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.User;
import com.gushwork.SlotMachine.service.SlotMachineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/slot-machine")
public class SlotMachineController {
    private final SlotMachineService slotMachineService;

    public SlotMachineController(SlotMachineService slotMachineService) {
        this.slotMachineService = slotMachineService;
    }

    @PostMapping("/start")
    public Session startGame(@Valid @RequestBody User user) {
        return slotMachineService.createSession(user);
    }

    @PostMapping("/roll/{sessionId}")
    public RollResult roll(@PathVariable String sessionId) {
        return slotMachineService.roll(sessionId);
    }

    @PostMapping("/cash-out/{sessionId}")
    public int cashOut(@PathVariable String sessionId) {
        return slotMachineService.cashOut(sessionId);
    }
}
