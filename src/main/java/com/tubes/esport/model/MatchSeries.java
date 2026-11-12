package com.tubes.esport.model;

import com.tubes.esport.exception.MatchPhaseViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchSeries {
    private final String matchId;
    private final String tournamentStage;
    private final int bestOf;
    private final Participant participantA;
    private final Participant participantB;
    private final List<GameRound> rounds;
    private MatchPhase phase;

    public MatchSeries(String matchId, String tournamentStage, int bestOf, Participant participantA, Participant participantB) {
        this.matchId = matchId;
        this.tournamentStage = tournamentStage;
        this.bestOf = bestOf;
        this.participantA = participantA;
        this.participantB = participantB;
        this.rounds = new ArrayList<>();
        this.phase = MatchPhase.SCHEDULED;
    }

    public GameRound recordRound(int participantAScore, int participantBScore, int durationMinutes) {
        if (!phase.allowsScoreInput()) {
            throw new MatchPhaseViolationException(matchId, phase, "RECORD_ROUND_SCORE");
        }

        String roundWinnerId = null;
        if (participantAScore > participantBScore) {
            roundWinnerId = participantA.getId();
        } else if (participantBScore > participantAScore) {
            roundWinnerId = participantB.getId();
        }

        int nextRoundNumber = rounds.size() + 1;
        GameRound round = new GameRound(nextRoundNumber, participantAScore, participantBScore, durationMinutes, roundWinnerId);
        this.rounds.add(round);

        int requiredWinsToClinch = (bestOf / 2) + 1;
        if (getParticipantAWins() >= requiredWinsToClinch || getParticipantBWins() >= requiredWinsToClinch || rounds.size() >= bestOf) {
            this.phase = MatchPhase.FINISHED;
        }

        return round;
    }

    public int getParticipantAWins() {
        return (int) rounds.stream()
                .filter(r -> participantA.getId().equals(r.getRoundWinnerId()))
                .count();
    }

    public int getParticipantBWins() {
        return (int) rounds.stream()
                .filter(r -> participantB.getId().equals(r.getRoundWinnerId()))
                .count();
    }

    public String getSeriesWinnerId() {
        if (phase != MatchPhase.FINISHED) {
            return null;
        }
        int winsA = getParticipantAWins();
        int winsB = getParticipantBWins();
        if (winsA > winsB) {
            return participantA.getId();
        } else if (winsB > winsA) {
            return participantB.getId();
        }
        return "DRAW";
    }

    public String getMatchId() {
        return matchId;
    }

    public String getTournamentStage() {
        return tournamentStage;
    }

    public int getBestOf() {
        return bestOf;
    }

    public Participant getParticipantA() {
        return participantA;
    }

    public Participant getParticipantB() {
        return participantB;
    }

    public List<GameRound> getRounds() {
        return Collections.unmodifiableList(rounds);
    }

    public MatchPhase getPhase() {
        return phase;
    }

    public void setPhase(MatchPhase phase) {
        this.phase = phase;
    }
}
