package com.tubes.esport.exception;

public class RosterIneligibleException extends RuntimeException {
    private final String participantId;
    private final String violationDetail;

    public RosterIneligibleException(String participantId, String violationDetail) {
        super("Peserta '" + participantId + "' tidak memenuhi syarat roster: " + violationDetail);
        this.participantId = participantId;
        this.violationDetail = violationDetail;
    }

    public String getParticipantId() {
        return participantId;
    }

    public String getViolationDetail() {
        return violationDetail;
    }
}
