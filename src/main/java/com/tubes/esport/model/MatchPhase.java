package com.tubes.esport.model;

public enum MatchPhase {
    SCHEDULED,
    LIVE,
    DISPUTED,
    FINISHED;

    public boolean allowsScoreInput() {
        return this == LIVE;
    }

    public boolean requiresAdminReview() {
        return this == DISPUTED;
    }
}
