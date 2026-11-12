package com.tubes.esport.repository;

import com.tubes.esport.model.MatchSeries;
import com.tubes.esport.model.Participant;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    List<Participant> findAllParticipants();
    Optional<Participant> findParticipantById(String participantId);
    Participant saveParticipant(Participant participant);

    List<MatchSeries> findAllMatches();
    Optional<MatchSeries> findMatchById(String matchId);
    MatchSeries saveMatch(MatchSeries matchSeries);
}
