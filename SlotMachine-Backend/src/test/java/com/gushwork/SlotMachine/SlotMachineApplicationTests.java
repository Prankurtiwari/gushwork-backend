package com.gushwork.SlotMachine;

import com.gushwork.SlotMachine.model.RollResult;
import com.gushwork.SlotMachine.model.Session;
import com.gushwork.SlotMachine.model.Symbol;
import com.gushwork.SlotMachine.respository.SessionRepository;
import com.gushwork.SlotMachine.service.RollStrategy;
import com.gushwork.SlotMachine.service.SlotMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SlotMachineApplicationTests {

	@Mock
	private SessionRepository sessionRepository;

	@Mock
	private RollStrategy rollStrategy;

	@InjectMocks
	private SlotMachineService slotMachineService;

	private Session testSession;

	@BeforeEach
	public void setUp() {
		testSession = new Session();
	}

	@Test
	public void testCreateSession() {
		// When repository.createSession() is called, return a new test session.
		when(sessionRepository.createSession()).thenReturn(testSession);
		Session session = slotMachineService.createSession();
		assertNotNull(session.getSessionId());
		assertEquals(10, session.getCredits());
		verify(sessionRepository, times(1)).createSession();
	}

	@Test
	public void testRollWinningOutcome() {
		// Create a spy of the service to override generateRandomSymbols for deterministic behavior.
		SlotMachineService spyService = spy(slotMachineService);
		// Set up the session: starting with 10 credits.
		String sessionId = testSession.getSessionId();
		testSession.setCredits(10);

		// When repository.getSession is called, return the test session.
		when(sessionRepository.getSession(sessionId)).thenReturn(testSession);
		// Simulate a winning combination.
		List<Symbol> winningSymbols = Arrays.asList(Symbol.CHERRY, Symbol.CHERRY, Symbol.CHERRY);
		doReturn(winningSymbols).when(spyService).generateRandomSymbols();
		// Ensure the roll strategy does not force a roll.
		when(rollStrategy.shouldRoll(testSession.getCredits())).thenReturn(false);

		RollResult result = spyService.roll(sessionId);
		assertTrue(result.isWinner());
		assertEquals(Symbol.CHERRY.getReward(), result.getReward());
		// Calculation: starting credits 10, deduct 1 for roll, add reward 10 ⇒ 10 - 1 + 10 = 19.
		assertEquals(19, testSession.getCredits());
	}

	@Test
	public void testRollLosingOutcome() {
		SlotMachineService spyService = spy(slotMachineService);
		String sessionId = testSession.getSessionId();
		testSession.setCredits(10);
		when(sessionRepository.getSession(sessionId)).thenReturn(testSession);
		// Simulate a losing combination.
		List<Symbol> losingSymbols = Arrays.asList(Symbol.CHERRY, Symbol.LEMON, Symbol.ORANGE);
		doReturn(losingSymbols).when(spyService).generateRandomSymbols();
		// For a loss, the roll strategy should not trigger.
		when(rollStrategy.shouldRoll(anyInt())).thenReturn(false);

		RollResult result = spyService.roll(sessionId);
		assertFalse(result.isWinner());
		assertEquals(0, result.getReward());
		// Credits: 10 - 1 = 9.
		assertEquals(9, testSession.getCredits());
	}

	@Test
	public void testRollWithForcedRoll() {
		SlotMachineService spyService = spy(slotMachineService);
		// Prepare a session with higher credits (e.g., 50 credits) to meet a possible cheat threshold.
		String sessionId = testSession.getSessionId();
		testSession.setCredits(50);
		when(sessionRepository.getSession(sessionId)).thenReturn(testSession);
		// Simulate an initial winning combination.
		List<Symbol> winningSymbols = Arrays.asList(Symbol.WATERMELON, Symbol.WATERMELON, Symbol.WATERMELON);
		doReturn(winningSymbols).when(spyService).generateRandomSymbols();
		// Force a roll by making the strategy always return true.
		when(rollStrategy.shouldRoll(testSession.getCredits())).thenReturn(true);

		RollResult result = spyService.roll(sessionId);
		// Even though the first roll produced a winning combination, the forced roll cancels it.
		assertFalse(result.isWinner());
		assertEquals(0, result.getReward());
		// The only deduction is for the roll cost: 50 - 1 = 49.
		assertEquals(49, testSession.getCredits());
	}

	@Test
	public void testCashOut() {
		String sessionId = testSession.getSessionId();
		testSession.setCredits(50);
		when(sessionRepository.getSession(sessionId)).thenReturn(testSession);

		int cashedOut = slotMachineService.cashOut(sessionId);
		assertEquals(50, cashedOut);
		verify(sessionRepository, times(1)).deleteSession(sessionId);
	}

}
