package org.example.trainerworkloadservice.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ActionType;
import org.example.trainerworkloadservice.model.TrainerSummaryDynamo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerSummaryService {

    public static final Logger log = LoggerFactory.getLogger(TrainerSummaryService.class);
    private final DynamoDBMapper mapper;

    @Autowired
    public TrainerSummaryService(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public void processTraining(InputDto input) {
        if (input == null) {
            throw new IllegalArgumentException("InputDto is null");
        }

        if (input.getUsername() == null || input.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
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

        log.info("Processing training {}", input);

        LocalDate date = input.getTrainingDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());

        TrainerSummaryDynamo trainer = mapper.load(TrainerSummaryDynamo.class,input.getUsername());
        if(trainer == null) {
            TrainerSummaryDynamo t = new TrainerSummaryDynamo();
            t.setTrainerUsername(input.getUsername());
            t.setFirstName(input.getFirstName());
            t.setLastName(input.getLastName());
            t.setIsActive(input.isActive());
            t.setYearlyMonthlyDuration(new ConcurrentHashMap<>());
            trainer=t;
        }

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

        mapper.save(trainer);

        log.info("-------------------------------- Successfully processed training ------------------------------------ ");
    }

    public TrainerSummaryDynamo getTrainerSummary(String username) {
        return mapper.load(TrainerSummaryDynamo.class,username);
    }
}
