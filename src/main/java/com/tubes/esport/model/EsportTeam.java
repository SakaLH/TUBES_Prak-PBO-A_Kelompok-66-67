package com.tubes.esport.model;

import com.tubes.esport.exception.RosterIneligibleException;

public class EsportTeam extends Participant {
    private static final int MIN_ROSTER_SIZE = 5;

    private final int rosterSize;
    private final String captainName;

    public EsportTeam(String id, String displayName, String region, int rosterSize, String captainName) {
        super(id, displayName, region);
        this.rosterSize = rosterSize;
        this.captainName = captainName;
    }

    public int getRosterSize() {
        return rosterSize;
    }

    public String getCaptainName() {
        return captainName;
    }

    @Override
    public String getCategory() {
        return "TEAM_5V5";
    }

    @Override
    public void validateRosterRule() {
        if (rosterSize < MIN_ROSTER_SIZE) {
            throw new RosterIneligibleException(
                    getId(),
                    "Jumlah roster tim minimal " + MIN_ROSTER_SIZE + " pemain (terdaftar: " + rosterSize + ")"
            );
        }
        if (captainName == null || captainName.isBlank()) {
            throw new RosterIneligibleException(getId(), "Nama kapten tim wajib ditentukan");
        }
    }
}
