package com.tubes.esport.strategy;

public interface StandingsPointRule {
    String getRuleName();
    int computePoints(int wins, int losses, int roundDiff);
}
