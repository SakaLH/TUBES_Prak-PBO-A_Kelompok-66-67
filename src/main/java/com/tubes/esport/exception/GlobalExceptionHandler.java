package com.tubes.esport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMatchNotFound(MatchNotFoundException ex) {
        Map<String, Object> details = Map.of("matchId", ex.getMatchId());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "MATCH_NOT_FOUND", ex.getMessage(), details);
    }

    @ExceptionHandler(RosterIneligibleException.class)
    public ResponseEntity<Map<String, Object>> handleRosterIneligible(RosterIneligibleException ex) {
        Map<String, Object> details = Map.of(
                "participantId", ex.getParticipantId(),
                "violationDetail", ex.getViolationDetail()
        );
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "ROSTER_INELIGIBLE", ex.getMessage(), details);
    }

    @ExceptionHandler(MatchPhaseViolationException.class)
    public ResponseEntity<Map<String, Object>> handlePhaseViolation(MatchPhaseViolationException ex) {
        Map<String, Object> details = Map.of(
                "matchId", ex.getMatchId(),
                "currentPhase", ex.getCurrentPhase().name(),
                "attemptedAction", ex.getAttemptedAction()
        );
        return buildErrorResponse(HttpStatus.CONFLICT, "MATCH_PHASE_VIOLATION", ex.getMessage(), details);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String errorCode, String message, Map<String, Object> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", errorCode);
        body.put("message", message);
        body.put("details", details);
        return ResponseEntity.status(status).body(body);
    }
}
