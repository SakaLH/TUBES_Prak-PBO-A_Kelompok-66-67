package com.tubes.esport.model;

import com.tubes.esport.exception.RosterIneligibleException;

public abstract class Participant {
    private final String id;
    private final String displayName;
    private final String region;

    protected Participant(String id, String displayName, String region) {
        this.id = id;
        this.displayName = displayName;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRegion() {
        return region;
    }

    public abstract String getCategory();

    public abstract void validateRosterRule() throws RosterIneligibleException;
}
