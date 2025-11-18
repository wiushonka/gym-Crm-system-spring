package com.example.controllers.rest;

import com.example.dto.trainee.*;
import com.example.dto.user.ChangeUserPasswordDTO;
import com.example.dto.user.CreatedUserDTO;
import com.example.converter.Converter;
import com.example.service.JwtService;
import com.example.service.TraineeService;
import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = {"application/json"})
@Tag(name = "Trainee Rest Controller", description = "Operations related to Trainee management and services")
public class TraineeRestController {

    private final TraineeService traineeService;
    private final Converter<TraineeRegistrationDTO, Trainee> fromDtoToTraineeConvertor;
    private final Converter<Trainee, TraineeDTO> traineeToTraineeDTOConvertor;
    private final Converter<UpdatedTraineeDTO,Trainee> traineeUpdatesToTraineeConvertor;
    private final Converter<Trainer, TrainerInTraineeDTO> trainerToTrainerInTraineeDTOConvertor;
    private final JwtService jwtService;
    private final Logger logger = LoggerFactory.getLogger(TraineeRestController.class);
    private final Counter counter;

    @Autowired
    public TraineeRestController(TraineeService traineeService,
                                 @Qualifier("traineeRegistrationTraineeConverter") Converter<TraineeRegistrationDTO, Trainee> fromDtoToTraineeConvertor,
                                 @Qualifier("traineeToTraineeDTOConverter") Converter<Trainee,TraineeDTO> traineeToTraineeDTOConvertor,
                                 @Qualifier("traineeUpdatesToTraineeConverter") Converter<UpdatedTraineeDTO,Trainee> traineeUpdatesToTraineeConventor,
                                 @Qualifier("trainerToTrainerInTraineeDTOConverter") Converter<Trainer,TrainerInTraineeDTO> trainerToTrainerInTraineeDTOConvertor,
                                 MeterRegistry meterRegistry,
                                 JwtService jwtService)
    {
        this.traineeService = traineeService;
        this.fromDtoToTraineeConvertor = fromDtoToTraineeConvertor;
        this.traineeToTraineeDTOConvertor = traineeToTraineeDTOConvertor;
        this.traineeUpdatesToTraineeConvertor = traineeUpdatesToTraineeConventor;
        this.trainerToTrainerInTraineeDTOConvertor=trainerToTrainerInTraineeDTOConvertor;
        this.jwtService = jwtService;
        logger.info("TraineeRestController created, dependencies injected");

        this.counter=Counter.builder("trainee_profile_updates_counter")
                            .tag("version","v1")
                            .description("Count of user Update calls")
                            .register(meterRegistry);
        logger.info("Trainee Updates Counter initialized ...");
        Gauge.builder("trainee_number_gauge", () -> traineeService.getAllTrainees().size())
                .tag("version", "v1")
                .description("total number of traineees")
                .register(meterRegistry);

        logger.info("Trainee Updates Gauge initialized ...");
    }

    @PostMapping()
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a trainee profile"),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error while creating trainee")
    })
    public ResponseEntity<EntityModel<CreatedUserDTO>> registerTrainee(@Valid @RequestBody TraineeRegistrationDTO registration) {
        Trainee trainee = fromDtoToTraineeConvertor.convert(registration);
        traineeService.createTraineeProfile(trainee);
        logger.info("TraineeRestController created with ID={}, username={}", trainee.getId(), trainee.getUserName());
        String token = jwtService.generateJwtToken(trainee.getUserName());
        EntityModel<CreatedUserDTO> model = EntityModel.of(new CreatedUserDTO(trainee.getUserName(),trainee.getPassword(),token));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{username}/password")
    @Operation(summary = "Update trainee password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully updated"),
            @ApiResponse(responseCode = "401", description = "User is not logged in"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile, op op op she maimuno !"),
            @ApiResponse(responseCode = "404", description = "Trainee not found or invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating password")
    })
    public ResponseEntity<Void> updateTraineePassword(@Valid @RequestBody ChangeUserPasswordDTO passChangeDto, Authentication auth) {
        String currUsername = auth.getName();
        if(!passChangeDto.getUsername().equals(currUsername)) {
            logger.warn("Can not update password for another user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Trainee> traineeOpt = traineeService.getTraineeProfile(passChangeDto.getUsername());
        if(traineeOpt.isEmpty() || !traineeService.checkIfValidTrainee(traineeOpt.get())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Trainee not found or invalid");
        }

        traineeService.updatePassword(traineeOpt.get(), passChangeDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainee profile", description = "Returns a trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee profile"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainee profile")
    })
    public ResponseEntity<TraineeDTO> getTraineeProfile(@PathVariable String username) {

        Trainee trainee = traineeService.getTraineeProfile(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Trainee not found"));

        return ResponseEntity.ok(traineeToTraineeDTOConvertor.convert(trainee));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainee profile", description = "Updates a trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found or updates failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating trainee profile")
    })
    public ResponseEntity<TraineeDTO> updateTraineeProfile(@PathVariable String username,
                                                           @Valid @RequestBody UpdatedTraineeDTO upds,
                                                           Authentication auth) {

        String currUsername = auth.getName();
        if(!currUsername.equals(username)) {
            logger.warn("Can not update profile for another user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Trainee updated = traineeUpdatesToTraineeConvertor.convert(upds);
        traineeService.updateTraineeProfileByUsername(username, updated);

        Trainee newTrainee = traineeService.getTraineeProfile(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Updates failed"));

        counter.increment();
        return ResponseEntity.ok(traineeToTraineeDTOConvertor.convert(newTrainee));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while deleting trainee profile")
    })
    public ResponseEntity<Void> deleteTraineeProfile(@PathVariable String username, Authentication auth) {
        String currUsername = auth.getName();
        if(!currUsername.equals(username)) {
            logger.warn("user {} tried to delete other trainee profile : {}", currUsername, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        traineeService.deleteTraineeProfile(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee's trainers list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated trainee's trainers list"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating trainers list")
    })
    public ResponseEntity<List<TrainerInTraineeDTO>> updateTraineesTrainersList(@PathVariable String username,
                                                                                @RequestBody List<String> trainerUsernames,
                                                                                Authentication auth) {
        String currUsername = auth.getName();
        if(!currUsername.equals(username)) {
            logger.warn("Can not update trainers List for another user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Trainer> trainerList = traineeService.updateTraineeTrainersList(username, trainerUsernames);
        List<TrainerInTraineeDTO> lst = new ArrayList<>();
        for(Trainer t : trainerList) {
            lst.add(trainerToTrainerInTraineeDTOConvertor.convert(t));
        }
        return ResponseEntity.ok(lst);
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Change trainee status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed trainee status"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while changing status")
    })
    public ResponseEntity<Void> changeStatus(@PathVariable String username, Authentication auth) {

        String currUsername = auth.getName();
        if(!currUsername.equals(username)) {
            logger.warn("Can not change status for another user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        traineeService.alterUserActivityByUsername(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee trainings"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainings")
    })
    public ResponseEntity<List<TraineeTrainingsDTO>> getTraineeTrainings(@PathVariable String username,
                                                                         @RequestParam(required = false) Date fromDate,
                                                                         @RequestParam(required = false) Date toDate,
                                                                         @RequestParam(required = false) String trainerName) {

        List<Training> result = traineeService.getTraineeTrainings(username);

        if (fromDate != null && toDate != null) {
            result.addAll(traineeService.getTraineeTrainingsBetweenDates(fromDate, toDate, username));
        } else if (fromDate != null) {
            result.addAll(traineeService.getTraineeTrainingsByFromDate(fromDate, username));
        } else if (toDate != null) {
            result.addAll(traineeService.getTraineeTrainingsByToDate(toDate, username));
        }
        if (trainerName != null) {
            result.addAll(traineeService.getTraineeTrainingsByTrainerUsername(trainerName, username));
        }

        List<Training> merged = result.stream().distinct().toList();
        List<TraineeTrainingsDTO> dtos = merged.stream()
                .map(training -> new TraineeTrainingsDTO(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getDuration(),
                        training.getTrainer().getUserName()))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{username}/not-assigned-trainers")
    @Operation(summary = "Get active trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved non-assigned active trainers"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainers")
    })
    public ResponseEntity<List<TrainerInTraineeDTO>> getNotAssignedOnTraineeActiveTrainers(@PathVariable String username) {
        List<Trainer> trainerList = traineeService.getTraineeUnassignedTrainers(username);
        List<TrainerInTraineeDTO> dtos = new ArrayList<>();
        for(Trainer t : trainerList) {
            dtos.add(trainerToTrainerInTraineeDTOConvertor.convert(t));
        }
        return ResponseEntity.ok(dtos);
    }
}
