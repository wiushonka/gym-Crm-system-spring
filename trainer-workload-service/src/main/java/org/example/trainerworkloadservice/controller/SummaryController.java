package org.example.trainerworkloadservice.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.trainerworkloadservice.dto.ActionType;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ResponseDto;
import org.example.trainerworkloadservice.model.TrainerSummaryMongo;
import org.example.trainerworkloadservice.service.TrainerSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CircuitBreaker(name = "trainerSummaryCB", fallbackMethod = "trainerServiceFallback")
@RestController
@RequestMapping("/trainers")
public class SummaryController {

    private final TrainerSummaryService service;

    @Autowired
    public SummaryController(TrainerSummaryService service) {
        this.service = service;
    }

    @PostMapping("/{username}/trainings")
    public ResponseEntity<ResponseDto> addTraining(
            @PathVariable String username,
            @RequestBody InputDto input) {

        input.setActionType(ActionType.ADD);
        input.setUsername(username);
        TrainerSummaryMongo updated = service.processTraining(input);

        ResponseDto response = new ResponseDto(
                updated.getTrainerUsername(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getIsActive(),
                updated.getYearlyMonthlyDuration()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{username}/trainings")
    public ResponseEntity<ResponseDto> deleteTraining(
            @PathVariable String username,
            @RequestBody InputDto input) {

        input.setActionType(ActionType.DELETE);
        input.setUsername(username);
        TrainerSummaryMongo updated = service.processTraining(input);

        ResponseDto response = new ResponseDto(
                updated.getTrainerUsername(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getIsActive(),
                updated.getYearlyMonthlyDuration()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}/summary")
    public ResponseEntity<ResponseDto> getSummary(@PathVariable String username) {
        TrainerSummaryMongo trainer = service.getTrainerSummary(username);
        ResponseDto response = new ResponseDto(
                trainer.getTrainerUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getIsActive(),
                trainer.getYearlyMonthlyDuration()
        );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> trainerServiceFallback(String username, InputDto input, Throwable ex) {
        return ResponseEntity
                .ok(new ResponseDto("Service unavailable: " + ex.getMessage(),
            "", "", false, null));
    }

    public ResponseEntity<ResponseDto> trainerServiceFallback(String username, Throwable ex) {
        return ResponseEntity
                .ok(new ResponseDto("Service unavailable: " + ex.getMessage(),
            "", "", false, null));
    }
}
