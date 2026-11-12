package com.tubes.esport.model;

import com.tubes.esport.exception.RosterIneligibleException;

public class SoloPlayer extends Participant {
    private final String inGameId;
    private final String rankTier;

    public SoloPlayer(String id, String displayName, String region, String inGameId, String rankTier) {
        super(id, displayName, region);
        this.inGameId = inGameId;
        this.rankTier = rankTier;
    }

    public String getInGameId() {
        return inGameId;
    }

    public String getRankTier() {
        return rankTier;
    }

    @Override
    public String getCategory() {
        return "SOLO_1V1";
    }

    @Override
    public void validateRosterRule() {
        if (getDisplayName() == null || getDisplayName().isBlank()) {
            throw new RosterIneligibleException(getId(), "Nama pemain solo wajib diisi");
        }
        if (inGameId == null || inGameId.isBlank()) {
            throw new RosterIneligibleException(getId(), "In-Game ID wajib terdaftar untuk kategori 1v1");
        }
        if (getRegion() == null || getRegion().isBlank()) {
            throw new RosterIneligibleException(getId(), "Region server pemain tidak boleh kosong");
        }
    }
}
