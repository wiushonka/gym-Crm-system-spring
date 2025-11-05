package com.example.controllers.rest;

import com.example.dto.trainings.TrainingTypeDTO;
import com.example.dto.trainings.newTrainingDTO;
import com.example.message.Producer;
import com.example.service.*;
import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.training.TrainingType;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import jakarta.validation.Valid;
import org.example.trainerworkloadservice.dto.ActionType;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping(value = "api/v1/trainings", produces = {"application/json"})
@Tag(name = "Training Management", description = "Endpoints for managing trainings and training types")
public class TrainingRestController {
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final Producer producer;

    private final Logger logger = LoggerFactory.getLogger(TrainingRestController.class);

    @Autowired
    public TrainingRestController(TrainingService trainingService, TraineeService traineeService,
                                  TrainerService trainerService, TrainingTypeService trainingTypeService,
                                  Producer producer) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.producer = producer;
    }

    @PostMapping()
    @Operation(summary = "Create a new training", description = "Adds a new training session for a trainee with a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "404", description = "Trainer or trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> addTraining(@Valid @RequestBody newTrainingDTO dto) {
        Optional<Trainee> traineeopt = traineeService.getTraineeProfile(dto.getTraineeUsername());
        Optional<Trainer> traineropt = trainerService.getTrainerProfile(dto.getTrainerUsername());

        if (traineeopt.isEmpty() || traineropt.isEmpty()) {
            logger.error("Trainer or trainee usernames are invalid, Trainee={} Trainer={}", dto.getTraineeUsername(), dto.getTrainerUsername());
            throw new IllegalArgumentException("Cannot add training with non-existing trainer or trainee");
        }

        Trainee trainee = traineeopt.get();
        Trainer trainer = traineropt.get();

        Training training = new Training(
                dto.getTrainingName(),
                dto.getTrainingStartDate(),
                dto.getTrainingDuration(),
                trainee,
                trainer.getSpecialization(),
                trainer);

        trainingService.addTraining(training);
        logger.info("New training added: {}", training.getTrainingName());

        InputDto inputDto = new InputDto.InputDtoBuilder()
                .active(trainer.isActive())
                .actionType(ActionType.ADD)
                .lastName(trainer.getLastName())
                .firstName(trainer.getFirstName())
                .username(trainer.getUserName())
                .trainingDuration(dto.getTrainingDuration())
                .trainingDate(dto.getTrainingStartDate())
                .build();
        logger.info("Sent add order to producer");
        producer.produce(inputDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    @Operation(summary = "Retrieve training types", description = "Returns a list of all available training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training types successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        List<TrainingType> lst = trainingTypeService.findAll();
        List<TrainingTypeDTO> dtos = new ArrayList<>();
        for (TrainingType t : lst) {
            dtos.add(new TrainingTypeDTO(t.getTrainingTypeName(), t.getId()));
        }
        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/summary/{username}")
    @Operation(summary = "Retrieve summary information", description = "Information about workload of some trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workload summary for the trainer"),
            @ApiResponse(responseCode = "400", description = "Invalid trainer username format or missing parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "404", description = "Trainer not found or no workload data available"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainer summary")
    })
    public ResponseDto getTrainingSummary(@PathVariable String username) {
        InputDto inputDto=new InputDto.InputDtoBuilder().username(username).actionType(ActionType.SUMMARY).build();
        return producer.produceReadRequest(inputDto);
    }

    @DeleteMapping()
    @Operation(summary = "Deletes Training", description = "Will delete training which has NOT already started")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training successfully deleted"),
            @ApiResponse(responseCode = "204", description = "Training not found or already started"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing training name in request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTraining(@RequestBody Map<String, String> request) {
        String trainingName = request.get("trainingName");

        if (trainingName == null || trainingName.isBlank()) {
            logger.warn("Missing or invalid trainingName in request body");
            return ResponseEntity.badRequest().build();
        }

        Optional<Training> trainingOpt = trainingService.getTrainingByName(trainingName);
        if(trainingOpt.isEmpty()){
            logger.info("Training with name={} not found", trainingName);
            return ResponseEntity.noContent().build();
        }

        Training training = trainingOpt.get();

        if(training.getTrainingDate().before(new Date())){
            logger.warn("Can not delete training which has already started");
            return ResponseEntity.noContent().build();
        }

        InputDto inputDto = new InputDto.InputDtoBuilder()
                .trainingDuration(training.getDuration())
                .trainingDate(training.getTrainingDate())
                .active(training.getTrainer().isActive())
                .username(training.getTrainer().getUserName())
                .lastName(training.getTrainer().getLastName())
                .firstName(training.getTrainer().getFirstName())
                .actionType(ActionType.DELETE)
                .build();
        logger.info("Sent delete order to producer");
        producer.produce(inputDto);

        trainingService.deleteTraining(trainingName);
        return ResponseEntity.ok().build();
    }
}
