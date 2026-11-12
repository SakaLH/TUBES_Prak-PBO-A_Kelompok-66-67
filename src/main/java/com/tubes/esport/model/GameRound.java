package com.tubes.esport.model;

public class GameRound {
    private final int roundNumber;
    private final int participantAScore;
    private final int participantBScore;
    private final int durationMinutes;
    private final String roundWinnerId;

    // Package-private: hanya dapat diciptakan oleh MatchSeries (relasi Composition)
    GameRound(int roundNumber, int participantAScore, int participantBScore, int durationMinutes, String roundWinnerId) {
        this.roundNumber = roundNumber;
        this.participantAScore = participantAScore;
        this.participantBScore = participantBScore;
        this.durationMinutes = durationMinutes;
        this.roundWinnerId = roundWinnerId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getParticipantAScore() {
        return participantAScore;
    }

    public int getParticipantBScore() {
        return participantBScore;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getRoundWinnerId() {
        return roundWinnerId;
    }
}
