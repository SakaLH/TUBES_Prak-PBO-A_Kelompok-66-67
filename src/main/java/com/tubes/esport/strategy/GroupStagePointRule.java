package com.tubes.esport.strategy;

import org.springframework.stereotype.Component;

@Component
public class GroupStagePointRule implements StandingsPointRule {

    @Override
    public String getRuleName() {
        return "GROUP_STAGE";
    }

    @Override
    public int computePoints(int wins, int losses, int roundDiff) {
        if (wins == 0 && losses == 0) {
            return 1; // Seri
        }
        return wins * 3; // Menang 3 poin, kalah 0 poin
    }
}
