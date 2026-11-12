package com.tubes.esport.exception;

import com.tubes.esport.model.MatchPhase;

public class MatchPhaseViolationException extends RuntimeException {
    private final String matchId;
    private final MatchPhase currentPhase;
    private final String attemptedAction;

    public MatchPhaseViolationException(String matchId, MatchPhase currentPhase, String attemptedAction) {
        super("Aksi '" + attemptedAction + "' ditolak pada match '" + matchId + "' karena fase saat ini adalah " + currentPhase);
        this.matchId = matchId;
        this.currentPhase = currentPhase;
        this.attemptedAction = attemptedAction;
    }

    public String getMatchId() {
        return matchId;
    }

    public MatchPhase getCurrentPhase() {
        return currentPhase;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }
}
