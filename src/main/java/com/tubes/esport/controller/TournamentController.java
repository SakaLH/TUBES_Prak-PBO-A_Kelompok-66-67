package com.tubes.esport.controller;

import com.tubes.esport.model.EsportTeam;
import com.tubes.esport.model.GameRound;
import com.tubes.esport.model.MatchPhase;
import com.tubes.esport.model.MatchSeries;
import com.tubes.esport.model.Participant;
import com.tubes.esport.model.SoloPlayer;
import com.tubes.esport.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {
    private final MatchService matchService;

    public TournamentController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/participants")
    public ResponseEntity<List<Participant>> getParticipants() {
        return ResponseEntity.ok(matchService.getAllParticipants());
    }

    @PostMapping("/participants/solo")
    public ResponseEntity<SoloPlayer> registerSolo(@RequestBody RegisterSoloRequest req) {
        SoloPlayer created = matchService.registerSoloPlayer(
                req.displayName(), req.region(), req.inGameId(), req.rankTier()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/participants/team")
    public ResponseEntity<EsportTeam> registerTeam(@RequestBody RegisterTeamRequest req) {
        EsportTeam created = matchService.registerEsportTeam(
                req.displayName(), req.region(), req.rosterSize(), req.captainName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/matches")
    public ResponseEntity<List<MatchSeries>> getMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/matches/{matchId}")
    public ResponseEntity<MatchSeries> getMatch(@PathVariable String matchId) {
        return ResponseEntity.ok(matchService.getMatchById(matchId));
    }

    @PostMapping("/matches")
    public ResponseEntity<MatchSeries> scheduleMatch(@RequestBody ScheduleMatchRequest req) {
        MatchSeries created = matchService.scheduleMatch(
                req.tournamentStage(), req.bestOf(), req.participantAId(), req.participantBId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/matches/{matchId}/phase")
    public ResponseEntity<MatchSeries> updatePhase(@PathVariable String matchId, @RequestBody UpdatePhaseRequest req) {
        return ResponseEntity.ok(matchService.updateMatchPhase(matchId, req.phase()));
    }

    @PostMapping("/matches/{matchId}/rounds")
    public ResponseEntity<GameRound> recordRound(@PathVariable String matchId, @RequestBody RecordRoundRequest req) {
        GameRound round = matchService.recordRoundResult(
                matchId, req.participantAScore(), req.participantBScore(), req.durationMinutes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(round);
    }

    @GetMapping("/standings")
    public ResponseEntity<List<Map<String, Object>>> getStandings(
            @RequestParam(defaultValue = "GROUP_STAGE") String rule) {
        return ResponseEntity.ok(matchService.getStandings(rule));
    }

    public record RegisterSoloRequest(String displayName, String region, String inGameId, String rankTier) {}
    public record RegisterTeamRequest(String displayName, String region, int rosterSize, String captainName) {}
    public record ScheduleMatchRequest(String tournamentStage, int bestOf, String participantAId, String participantBId) {}
    public record UpdatePhaseRequest(MatchPhase phase) {}
    public record RecordRoundRequest(int participantAScore, int participantBScore, int durationMinutes) {}
}
