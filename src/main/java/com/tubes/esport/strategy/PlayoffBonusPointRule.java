package com.tubes.esport.strategy;

import org.springframework.stereotype.Component;

@Component
public class PlayoffBonusPointRule implements StandingsPointRule {
    private static final int CLEAN_SWEEP_BONUS = 2;

    @Override
    public String getRuleName() {
        return "PLAYOFF_BONUS";
    }

    @Override
    public int computePoints(int wins, int losses, int roundDiff) {
        if (wins == 0 && losses == 0) {
            return 1;
        }
        int basePoints = wins * 3;
        boolean cleanSweep = wins > 0 && losses == 0 && roundDiff >= 2;
        return cleanSweep ? basePoints + CLEAN_SWEEP_BONUS : basePoints;
    }
}
