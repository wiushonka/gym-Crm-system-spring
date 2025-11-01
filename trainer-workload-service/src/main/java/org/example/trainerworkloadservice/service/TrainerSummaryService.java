package org.example.trainerworkloadservice.service;

import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ActionType;
import org.example.trainerworkloadservice.model.TrainerSummaryMongo;
import org.example.trainerworkloadservice.repository.SummaryRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerSummaryService {

    private final SummaryRepo repository;

    public TrainerSummaryService(SummaryRepo repository) {
        this.repository = repository;
    }

    public TrainerSummaryMongo processTraining(InputDto input) {
        if (input == null) {
            throw new IllegalArgumentException("InputDto is null");
        }
        if (input.getTrainingDate() == null) {
            throw new IllegalArgumentException("trainingDate is required");
        }
        if (input.getTrainingDuration() <= 0) {
            throw new IllegalArgumentException("trainingDuration must be > 0");
        }
        if (input.getActionType() == null) {
            throw new IllegalArgumentException("actionType is required");
        }
        if (input.getUsername() == null || input.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }

        LocalDate date = input.getTrainingDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());

        TrainerSummaryMongo trainer = repository.findByTrainerUsername(input.getUsername())
                .orElseGet(() -> {
                    TrainerSummaryMongo t = new TrainerSummaryMongo();
                    t.setTrainerUsername(input.getUsername());
                    t.setFirstName(input.getFirstName());
                    t.setLastName(input.getLastName());
                    t.setIsActive(input.isActive());
                    t.setYearlyMonthlyDuration(new ConcurrentHashMap<>());
                    return t;
                });

        trainer.setFirstName(input.getFirstName());
        trainer.setLastName(input.getLastName());
        trainer.setIsActive(input.isActive());

        Map<String, Map<String, Integer>> yearly = trainer.getYearlyMonthlyDuration();
        Map<String, Integer> monthMap = yearly.computeIfAbsent(year, y -> new ConcurrentHashMap<>());

        if (input.getActionType() == ActionType.ADD) {
            monthMap.merge(month, input.getTrainingDuration(), Integer::sum);
        } else {
            monthMap.computeIfPresent(month, (m, oldVal) -> {
                int updated = oldVal - input.getTrainingDuration();
                return updated > 0 ? updated : null;
            });
        }

        if (monthMap.isEmpty()) {
            yearly.remove(year);
        }

        return repository.save(trainer);
    }

    public TrainerSummaryMongo getTrainerSummary(String username) {
        return repository.findByTrainerUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));
    }

    public static class TrainerNotFoundException extends RuntimeException {
        public TrainerNotFoundException(String username) {
            super("Trainer not found: " + username);
        }
    }
}
