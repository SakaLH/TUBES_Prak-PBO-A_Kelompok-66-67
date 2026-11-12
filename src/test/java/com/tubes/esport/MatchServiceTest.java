package com.tubes.esport;

import com.tubes.esport.exception.MatchNotFoundException;
import com.tubes.esport.exception.MatchPhaseViolationException;
import com.tubes.esport.exception.RosterIneligibleException;
import com.tubes.esport.model.EsportTeam;
import com.tubes.esport.model.GameRound;
import com.tubes.esport.model.MatchPhase;
import com.tubes.esport.model.MatchSeries;
import com.tubes.esport.model.SoloPlayer;
import com.tubes.esport.repository.InMemoryMatchRepository;
import com.tubes.esport.service.MatchService;
import com.tubes.esport.strategy.GroupStagePointRule;
import com.tubes.esport.strategy.PlayoffBonusPointRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MatchServiceTest {

    private MatchService matchService;

    @BeforeEach
    void setUp() {
        InMemoryMatchRepository repository = new InMemoryMatchRepository();
        matchService = new MatchService(
                repository,
                List.of(new GroupStagePointRule(), new PlayoffBonusPointRule())
        );
    }

    @Test
    void enumMatchPhaseBehaviorsWorkAsExpected() {
        assertTrue(MatchPhase.LIVE.allowsScoreInput());
        assertFalse(MatchPhase.SCHEDULED.allowsScoreInput());
        assertFalse(MatchPhase.DISPUTED.allowsScoreInput());
        assertFalse(MatchPhase.FINISHED.allowsScoreInput());

        assertTrue(MatchPhase.DISPUTED.requiresAdminReview());
        assertFalse(MatchPhase.LIVE.requiresAdminReview());
    }

    @Test
    void esportTeamValidationThrowsRosterIneligibleWhenUnderMinimumSize() {
        RosterIneligibleException ex = assertThrows(
                RosterIneligibleException.class,
                () -> matchService.registerEsportTeam("Understaffed Squad", "SEA", 3, "Rizky")
        );
        assertNotNull(ex.getParticipantId());
        assertTrue(ex.getViolationDetail().contains("minimal 5"));
    }

    @Test
    void soloPlayerRegistrationSucceedsWhenValid() {
        SoloPlayer player = matchService.registerSoloPlayer("Fajar", "SEA", "Fajar#99", "Radiant");
        assertEquals("SOLO_1V1", player.getCategory());
        assertEquals("Fajar", player.getDisplayName());
    }

    @Test
    void groupStagePointRuleComputesLeaguePoints() {
        GroupStagePointRule rule = new GroupStagePointRule();
        assertEquals(3, rule.computePoints(1, 0, 2)); // Menang
        assertEquals(1, rule.computePoints(0, 0, 0)); // Seri
        assertEquals(0, rule.computePoints(0, 1, -2)); // Kalah
    }

    @Test
    void playoffBonusPointRuleAddsBonusOnCleanSweep() {
        PlayoffBonusPointRule rule = new PlayoffBonusPointRule();
        assertEquals(5, rule.computePoints(1, 0, 2)); // Menang telak tanpa kalah ronde (+2 bonus)
        assertEquals(3, rule.computePoints(1, 0, 1)); // Menang 2-1 (kehilangan 1 ronde, tanpa bonus)
        assertEquals(0, rule.computePoints(0, 1, -2)); // Kalah
    }

    @Test
    void recordRoundThrowsPhaseViolationWhenMatchIsNotLive() {
        MatchSeries scheduled = matchService.scheduleMatch("QUARTER_FINAL", 3, "TM-01", "TM-02");
        MatchPhaseViolationException ex = assertThrows(
                MatchPhaseViolationException.class,
                () -> matchService.recordRoundResult(scheduled.getMatchId(), 13, 10, 40)
        );
        assertEquals(scheduled.getMatchId(), ex.getMatchId());
        assertEquals(MatchPhase.SCHEDULED, ex.getCurrentPhase());
        assertEquals("RECORD_ROUND_SCORE", ex.getAttemptedAction());
    }

    @Test
    void getMatchByIdThrowsMatchNotFoundExceptionForUnknownId() {
        MatchNotFoundException ex = assertThrows(
                MatchNotFoundException.class,
                () -> matchService.getMatchById("MCH-999")
        );
        assertEquals("MCH-999", ex.getMatchId());
    }

    @Test
    void recordingClinchingRoundsFinishesSeriesAndUpdatesStandings() {
        GameRound r1 = matchService.recordRoundResult("MCH-002", 13, 5, 35);
        assertEquals(1, r1.getRoundNumber());
        assertEquals("TM-02", r1.getRoundWinnerId());

        matchService.recordRoundResult("MCH-002", 13, 8, 39);
        MatchSeries updated = matchService.getMatchById("MCH-002");
        assertEquals(MatchPhase.FINISHED, updated.getPhase());
        assertEquals(2, updated.getRounds().size());

        List<Map<String, Object>> playoffStandings = matchService.getStandings("PLAYOFF_BONUS");
        assertFalse(playoffStandings.isEmpty());
        assertEquals(5, playoffStandings.get(0).get("points"));
    }
}
