package com.tubes.esport.strategy;

import org.springframework.stereotype.Component;

@Component
public class GroupStagePointRule implements StandingsPointRule {

    @Override
    public String getRuleName() {
        return "GROUP_STAGE";
    }

    @Override
    public int computePoints(int matchWins, int matchLosses, int roundDiff) {
        // Sesuaikan rumus poin babak grup 
        // menang dapat 3 poin, seri 1 poin, kalah 0 poin
        if (matchWins > matchLosses) {
            return 3;
        } else if (matchWins == matchLosses) {
            return 1;
        }
        return 0;
    }
}