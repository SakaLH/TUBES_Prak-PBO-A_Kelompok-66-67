package com.tubes.esport.strategy;

import org.springframework.stereotype.Component;

@Component
public class PlayoffBonusPointRule implements StandingsPointRule {

    @Override
    public String getRuleName() {
        return "PLAYOFF_BONUS";
    }

    @Override
    public int computePoints(int matchWins, int matchLosses, int roundDiff) {
        // Sesuaikan rumus poin + bonus playoff 
        // menang dapat 3 poin + bonus dari selisih ronde positif
        if (matchWins > matchLosses) {
            return 3 + Math.max(0, roundDiff);
        } else if (matchWins == matchLosses) {
            return 1;
        }
        return 0;
    }
}