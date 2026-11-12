package com.tubes.esport.service;

import com.tubes.esport.exception.MatchNotFoundException;
import com.tubes.esport.exception.MatchPhaseViolationException;
import com.tubes.esport.exception.RosterIneligibleException;
import com.tubes.esport.model.EsportTeam;
import com.tubes.esport.model.GameRound;
import com.tubes.esport.model.MatchPhase;
import com.tubes.esport.model.MatchSeries;
import com.tubes.esport.model.Participant;
import com.tubes.esport.model.SoloPlayer;
import com.tubes.esport.repository.MatchRepository;
import com.tubes.esport.strategy.StandingsPointRule;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final Map<String, StandingsPointRule> pointRules;

    public MatchService(MatchRepository matchRepository, List<StandingsPointRule> rules) {
        this.matchRepository = matchRepository;
        this.pointRules = rules.stream()
                .collect(Collectors.toMap(r -> r.getRuleName().toUpperCase(), Function.identity()));
    }

    public List<Participant> getAllParticipants() {
        return matchRepository.findAllParticipants();
    }

    public SoloPlayer registerSoloPlayer(String displayName, String region, String inGameId, String rankTier) {
        String id = "PL-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        SoloPlayer player = new SoloPlayer(id, displayName, region, inGameId, rankTier);
        player.validateRosterRule();
        matchRepository.saveParticipant(player);
        return player;
    }

    public EsportTeam registerEsportTeam(String displayName, String region, int rosterSize, String captainName) {
        String id = "TM-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        EsportTeam team = new EsportTeam(id, displayName, region, rosterSize, captainName);
        team.validateRosterRule();
        matchRepository.saveParticipant(team);
        return team;
    }

    public List<MatchSeries> getAllMatches() {
        return matchRepository.findAllMatches();
    }

    public MatchSeries getMatchById(String matchId) {
        return matchRepository.findMatchById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
    }

    public MatchSeries scheduleMatch(String tournamentStage, int bestOf, String participantAId, String participantBId) {
        Participant participantA = matchRepository.findParticipantById(participantAId)
                .orElseThrow(() -> new RosterIneligibleException(participantAId, "Peserta A belum terdaftar"));
        Participant participantB = matchRepository.findParticipantById(participantBId)
                .orElseThrow(() -> new RosterIneligibleException(participantBId, "Peserta B belum terdaftar"));

        if (participantA.getId().equals(participantB.getId())) {
            throw new RosterIneligibleException(participantAId,
                    "Peserta tidak boleh bertanding melawan dirinya sendiri");
        }

        participantA.validateRosterRule();
        participantB.validateRosterRule();

        String matchId = "MCH-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        MatchSeries series = new MatchSeries(matchId, tournamentStage, bestOf, participantA, participantB);
        return matchRepository.saveMatch(series);
    }

    public MatchSeries updateMatchPhase(String matchId, MatchPhase targetPhase) {
        MatchSeries series = getMatchById(matchId);
        if (series.getPhase() == MatchPhase.FINISHED && targetPhase != MatchPhase.DISPUTED) {
            throw new MatchPhaseViolationException(matchId, series.getPhase(), "CHANGE_PHASE_TO_" + targetPhase.name());
        }
        series.setPhase(targetPhase);
        return matchRepository.saveMatch(series);
    }

    public GameRound recordRoundResult(String matchId, int participantAScore, int participantBScore,
            int durationMinutes) {
        MatchSeries series = getMatchById(matchId);
        GameRound recorded = series.recordRound(participantAScore, participantBScore, durationMinutes);
        matchRepository.saveMatch(series);
        return recorded;
    }

    public List<Map<String, Object>> getStandings(String ruleName) {
        StandingsPointRule rule = pointRules.getOrDefault(
                ruleName == null ? "GROUP_STAGE" : ruleName.toUpperCase(),
                pointRules.get("GROUP_STAGE"));

        Map<String, int[]> statsByParticipant = new LinkedHashMap<>();
        for (Participant p : matchRepository.findAllParticipants()) {
            // [0]=matchWins, [1]=matchLosses, [2]=roundDiff, [3]=totalPoints
            statsByParticipant.put(p.getId(), new int[] { 0, 0, 0, 0 });
        }

        for (MatchSeries match : matchRepository.findAllMatches()) {
            if (match.getPhase() != MatchPhase.FINISHED) {
                continue;
            }
            String idA = match.getParticipantA().getId();
            String idB = match.getParticipantB().getId();
            statsByParticipant.putIfAbsent(idA, new int[] { 0, 0, 0, 0 });
            statsByParticipant.putIfAbsent(idB, new int[] { 0, 0, 0, 0 });

            int roundsA = match.getParticipantAWins();
            int roundsB = match.getParticipantBWins();
            int diffA = roundsA - roundsB;
            int diffB = roundsB - roundsA;

            int[] statA = statsByParticipant.get(idA);
            int[] statB = statsByParticipant.get(idB);

            statA[2] += diffA;
            statB[2] += diffB;

            if (roundsA > roundsB) {
                statA[0] += 1;
                statB[1] += 1;
                statA[3] += rule.computePoints(1, 0, diffA);
                statB[3] += rule.computePoints(0, 1, diffB);
            } else if (roundsB > roundsA) {
                statB[0] += 1;
                statA[1] += 1;
                statB[3] += rule.computePoints(1, 0, diffB);
                statA[3] += rule.computePoints(0, 1, diffA);
            } else {
                statA[3] += rule.computePoints(0, 0, 0);
                statB[3] += rule.computePoints(0, 0, 0);
            }
        }

        return statsByParticipant.entrySet().stream()
                .map(entry -> {
                    String pid = entry.getKey();
                    int[] s = entry.getValue();
                    String name = matchRepository.findParticipantById(pid)
                            .map(Participant::getDisplayName)
                            .orElse(pid);
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("participantId", pid);
                    row.put("displayName", name);
                    row.put("matchWins", s[0]);
                    row.put("matchLosses", s[1]);
                    row.put("roundDiff", s[2]);
                    row.put("points", s[3]);
                    row.put("appliedRule", rule.getRuleName());
                    return row;
                })
                .collect(Collectors.toList());
    }
}
