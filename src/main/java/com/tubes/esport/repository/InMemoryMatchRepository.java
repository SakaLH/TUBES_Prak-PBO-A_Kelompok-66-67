package com.tubes.esport.repository;

import com.tubes.esport.model.EsportTeam;
import com.tubes.esport.model.MatchPhase;
import com.tubes.esport.model.MatchSeries;
import com.tubes.esport.model.Participant;
import com.tubes.esport.model.SoloPlayer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMatchRepository implements MatchRepository {
    private final Map<String, Participant> participants = new ConcurrentHashMap<>();
    private final Map<String, MatchSeries> matches = new ConcurrentHashMap<>();

    public InMemoryMatchRepository() {
        seedDummyData();
    }

    private void seedDummyData() {
        EsportTeam teamAlpha = new EsportTeam("TM-01", "Garuda Cyber", "SEA", 5, "Arif Nugroho");
        EsportTeam teamBravo = new EsportTeam("TM-02", "Nusantara Strikers", "SEA", 6, "Bima Sakti");
        SoloPlayer soloOne = new SoloPlayer("PL-01", "Zaki Pratama", "SEA", "Zaki#ID01", "Immortal");

        participants.put(teamAlpha.getId(), teamAlpha);
        participants.put(teamBravo.getId(), teamBravo);
        participants.put(soloOne.getId(), soloOne);

        // Match 1: Sudah selesai dengan kemenangan telak 2-0
        MatchSeries match1 = new MatchSeries("MCH-001", "GROUP_A", 3, teamAlpha, teamBravo);
        match1.setPhase(MatchPhase.LIVE);
        match1.recordRound(13, 7, 38);
        match1.recordRound(13, 9, 41);
        matches.put(match1.getMatchId(), match1);

        // Match 2: Sedang LIVE siap menerima input skor ronde
        MatchSeries match2 = new MatchSeries("MCH-002", "GROUP_A", 3, teamBravo, teamAlpha);
        match2.setPhase(MatchPhase.LIVE);
        matches.put(match2.getMatchId(), match2);
    }

    @Override
    public List<Participant> findAllParticipants() {
        return new ArrayList<>(participants.values());
    }

    @Override
    public Optional<Participant> findParticipantById(String participantId) {
        return Optional.ofNullable(participants.get(participantId));
    }

    @Override
    public Participant saveParticipant(Participant participant) {
        participants.put(participant.getId(), participant);
        return participant;
    }

    @Override
    public List<MatchSeries> findAllMatches() {
        return new ArrayList<>(matches.values());
    }

    @Override
    public Optional<MatchSeries> findMatchById(String matchId) {
        return Optional.ofNullable(matches.get(matchId));
    }

    @Override
    public MatchSeries saveMatch(MatchSeries matchSeries) {
        matches.put(matchSeries.getMatchId(), matchSeries);
        return matchSeries;
    }
}
