package com.tubes.esport.exception;

public class MatchNotFoundException extends RuntimeException {
    private final String matchId;

    public MatchNotFoundException(String matchId) {
        super("Pertandingan dengan ID '" + matchId + "' tidak ditemukan");
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }
}
