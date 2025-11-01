package com.example.controllers.rest;

import com.example.dto.trainer.TrainerDTO;
import com.example.dto.trainer.TrainerTrainingsDTO;
import com.example.dto.trainer.UpdatedTrainerDTO;
import com.example.dto.user.ChangeUserPasswordDTO;
import com.example.dto.user.CreatedUserDTO;
import com.example.dto.trainer.TrainerRegistrationDTO;
import com.example.converter.Converter;
import com.example.service.JwtService;
import com.example.service.TrainerService;
import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping(value = "/api/v1/trainers", produces = {"application/json"} )
@Tag(name = "Trainer Controller", description = "Operations related to Trainer management and services")
public class TrainerRestController {
    private final TrainerService trainerService;
    private final Converter<TrainerRegistrationDTO, Trainer> fromDtoToTrainerConvertor;
    private final Converter<Trainer,TrainerDTO> trainerToTrainerDtoConvertor;
    private final Converter<UpdatedTrainerDTO,Trainer> trainerUpdatesToTrainerConvertor;
    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(TrainerRestController.class);
    private final Counter counter;

    @Autowired
    public TrainerRestController(TrainerService trainerService,
                                 @Qualifier("trainerRegistrationTrainerConverter") Converter<TrainerRegistrationDTO, Trainer> converter,
                                 @Qualifier("trainerToTrainerDTOConverter") Converter<Trainer,TrainerDTO> trainerToTrainerDtoConvertor,
                                 @Qualifier("trainerUpdatesToTrainerConverter") Converter<UpdatedTrainerDTO,Trainer> trainerUpdatesToTrainerConvertor,
                                 MeterRegistry registry,
                                 JwtService jwtService)
    {
        this.trainerService = trainerService;
        this.fromDtoToTrainerConvertor = converter;
        this.trainerToTrainerDtoConvertor = trainerToTrainerDtoConvertor;
        this.trainerUpdatesToTrainerConvertor = trainerUpdatesToTrainerConvertor;
        this.jwtService = jwtService;
        counter = Counter.builder("trainer_trainings_fetched")
                        .tag("version","v1")
                        .description("Number of times trainer trainings were fetched")
                        .register(registry);

        logger.info("Trainer Trainings Counter Initialized ...");

        Gauge.builder("total_number_of_trainers", () -> trainerService.getAllTrainers().stream().filter(Trainer::isActive).toList().size())
                .tag("version", "v1")
                .description("number of trainers")
                .register(registry);
        logger.info("Trainer Gauge Initialized ...");
    }

    @PostMapping()
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a trainer profile"),
            @ApiResponse(responseCode = "400", description = "Invalid request body supplied"),
            @ApiResponse(responseCode = "500", description = "Internal server error while creating trainer")
    })
    public ResponseEntity<EntityModel<CreatedUserDTO>> registerTrainer(@Valid @RequestBody TrainerRegistrationDTO registration) {
        Trainer trainer = fromDtoToTrainerConvertor.convert(registration);
        trainerService.createTrainerProfileWithSpecialization(trainer,registration.getSpecialization());
        logger.info("TrainerRestController created with ID={}, username={}", trainer.getId(), trainer.getUserName());
        String token = jwtService.generateJwtToken(trainer.getUserName());
        EntityModel<CreatedUserDTO> model = EntityModel.of(new CreatedUserDTO(trainer.getUserName(),trainer.getPassword(),token));

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{username}/password")
    @Operation(summary = "Update trainer password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully updated"),
            @ApiResponse(responseCode = "401", description = "User is not logged in"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found or invalid"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating password")
    })
    public ResponseEntity<Void> updateTrainerPassword(@Valid @RequestBody ChangeUserPasswordDTO passChangeDto , Authentication auth) {

        if(!passChangeDto.getUsername().equals(auth.getName())) {
            logger.warn("Username is not logged in, cant change password for other users");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Trainer> trainerOpt = trainerService.getTrainerProfile(passChangeDto.getUsername());
        if(trainerOpt.isEmpty() || !trainerService.isValidTrainer(trainerOpt.get().getUserName(),trainerOpt.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Trainer not found or invalid");
        }

        trainerService.updatePassword(trainerOpt.get().getUserName(), passChangeDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile", description = "Returns a trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer profile"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainer profile")
    })
    public ResponseEntity<TrainerDTO> getTrainerProfile(@PathVariable String username) {

        Trainer trainer = trainerService.getTrainerProfile(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Trainer not found"));

        return ResponseEntity.ok(trainerToTrainerDtoConvertor.convert(trainer));
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update trainer profile", description = "Updates a trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found or updates failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error while updating trainer profile")
    })
    public ResponseEntity<TrainerDTO> updateTrainerProfile(@PathVariable String username,
                                                           @Valid @RequestBody UpdatedTrainerDTO upds,
                                                           Authentication auth) {

        String curUsername = auth.getName();
        if(!username.equals(curUsername)) {
            logger.warn("Username is not logged in, cant update another trainers profile");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Trainer trainer = trainerService.getTrainerProfile(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Trainer not found"));

        Trainer updated = trainerUpdatesToTrainerConvertor.convert(upds);
        trainerService.updateTrainerProfileWithSpecialization(trainer.getId(),updated,upds.getSpecializationName());

        Trainer newTrainer = trainerService.getTrainerProfile(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Updates failed"));

        return ResponseEntity.ok(trainerToTrainerDtoConvertor.convert(newTrainer));
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "Change trainer status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully changed trainer status"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while changing status")
    })
    public ResponseEntity<Void> changeStatus(@PathVariable String username, Authentication auth) {

        String curUsername = auth.getName();
        if(!username.equals(curUsername)) {
            logger.warn("Username is not logged in, cant change status");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        trainerService.alterUserActivity(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer trainings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainer trainings"),
            @ApiResponse(responseCode = "403", description = "Access denied for another user's profile"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error while fetching trainings")
    })
    public ResponseEntity<List<TrainerTrainingsDTO>> getTrainerTrainings(@PathVariable String username,
                                                                         @RequestParam(required = false) Date fromDate,
                                                                         @RequestParam(required = false) Date toDate,
                                                                         @RequestParam(required = false) String traineeName) {

        List<Training> result = trainerService.getAllTrainings(username);

        if (fromDate != null && toDate != null) {
            result.addAll(trainerService.getTrainerTrainingsBetweenDates(fromDate, toDate, username));
        } else if (fromDate != null) {
            result.addAll(trainerService.getTrainerTrainingsByFromDate(fromDate, username));
        } else if (toDate != null) {
            result.addAll(trainerService.getTrainerTrainingsByToDate(toDate, username));
        }
        if (traineeName != null) {
            result.addAll(trainerService.getTrainerTrainingsByTraineeUsername(traineeName, username));
        }

        List<Training> merged = result.stream().distinct().toList();
        List<TrainerTrainingsDTO> dtos = merged.stream()
                .map(training -> new TrainerTrainingsDTO(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getDuration(),
                        training.getTrainee().getUserName()))
                .toList();

        counter.increment();
        return ResponseEntity.ok(dtos);
    }
}
