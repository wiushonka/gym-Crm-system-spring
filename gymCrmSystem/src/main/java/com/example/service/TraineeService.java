package com.example.service;

import com.example.dao.TraineeDAO;
import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import com.example.utili.Utili;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDAO dao;
    protected PasswordEncoder encoder;

    @Autowired
    public void setTraineeDao(TraineeDAO dao){
        this.dao = dao;
        logger.debug("TraineeDAO injected into TraineeService");
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder encoder){
        this.encoder = encoder;
    }

    public void createTraineeProfile(@NotNull Trainee trainee) {
        String firstName=trainee.getFirstName().toLowerCase();
        String lastName=trainee.getLastName().toLowerCase();
        String password = Utili.generatePassword();
        String username = Utili.generateUsername(firstName,lastName,
                                            genName->dao.getTraineeByUsername(genName).isPresent());
        trainee.setUserName(username);
        trainee.setPassword(encoder.encode(password));
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);

        dao.addTrainee(trainee);
        logger.info("Created trainee profile: username={}, firstName={}, lastName={}", username, firstName, lastName);
    }

    public void deleteTraineeProfile(String username){
        Optional<Trainee> trainee = dao.getTraineeByUsername(username);
        if (trainee.isEmpty()) {
            logger.warn("Attempted to delete non-existent trainee: username={}", username);
            return;
        }
        dao.removeTraineeById(trainee.get().getId());
        logger.info("Deleted trainee profile: username={}", username);
    }

    public void updateTraineeProfileByUsername(@NotNull String username, @NotNull Trainee newTrainee){
        Optional<Trainee> trainee = dao.getTraineeByUsername(username);
        if (trainee.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee: username={}", username);
            return;
        }
        dao.updateTrainee(trainee.get().getId(), newTrainee);
        logger.info("Updated trainee profile by Username: username={}", username);
    }

    public void updateTraineeProfile(@NotNull Trainee trainee){
        Long id = trainee.getId();
        Optional<Trainee> old = dao.getTraineeById(id);
        if (old.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee: id={}", id);
            return;
        }
        dao.updateTrainee(id, trainee);
        logger.info("Updated trainee profile: id={}, username={}", id, trainee.getUserName());
    }

    public void updateTraineeProfileById(@NotNull Long id, @NotNull Trainee trainee){
        Optional<Trainee> old = dao.getTraineeById(id);
        if (old.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee by ID: id={}", id);
            return;
        }
        dao.updateTrainee(id, trainee);
        logger.info("Updated trainee profile by ID: id={}, username={}", id, trainee.getUserName());
    }

    public Optional<Trainee> getTraineeProfile(String username){
        Optional<Trainee> traineeOpt = dao.getTraineeByUsername(username);
        if (traineeOpt.isPresent()) {
            logger.debug("Retrieved trainee profile: username={}", username);
            return traineeOpt;
        } else {
            logger.warn("No trainee found with username={}", username);
            return Optional.empty();
        }
    }

    public List<Trainee> getAllTrainees() {
        List<Trainee> all = dao.getAllTrainees();
        logger.debug("Retrieved all trainees, count={}", all.size());
        return all;
    }

    public void alterUserActivityByUsername(String username){
        Optional<Trainee> opt = dao.getTraineeByUsername(username);
        if (opt.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee: username={}", username);
            return;
        }
        Trainee trainee = opt.get();
        dao.alterUserActivity(trainee);
    }

    public void alterUserActivity(@NotNull Trainee trainee){ dao.alterUserActivity(trainee); }

    public Boolean checkIfValidUsernameAndPassword(@NotNull String username, @NotNull String password){
        Optional<Trainee> opt = dao.getTraineeByUsername(username);
        if (opt.isEmpty()) {
            logger.warn("no user with username = {} , so it can not be valid", username);
            return false;
        }
        Trainee trainee = opt.get();

        return encoder.matches(password, trainee.getPassword());
    }

    public Boolean checkIfValidTrainee(@NotNull Trainee trainee){ return dao.isValidTrainee(trainee); }

    public void updatePassword(@NotNull Trainee trainee, String newPassword) {
        dao.updateTraineePassword(trainee,encoder.encode(newPassword));
    }

    public List<Training> getTraineeTrainingsByTrainerUsername(String traineeUsername, String trainerUsername){
        return dao.getTraineeTrainingsByTrainerUsername(traineeUsername,trainerUsername);
    }

    public List<Training> getTraineeTrainingsByFromDate(Date cutOffDate, String username){
        return dao.getTraineeTrainingsByFromDate(cutOffDate,username);
    }

    public List<Training> getTraineeTrainingsByToDate(Date cutOffDate, String username){
        return dao.getTraineeTrainingsByToDate(cutOffDate, username);
    }
    public List<Training> getTraineeTrainingsBetweenDates(Date fromDate, Date toDate, String username){
        return dao.getTraineeTrainingsBetweenDates(fromDate,toDate,username);
    }

    public List<Trainer> getTraineeUnassignedTrainers(String username){
        Optional<Trainee> opt = dao.getTraineeByUsername(username);
        if (opt.isEmpty()) {
            logger.warn("Attempted to look up trainers for non-existent trainee: username={}", username);
            return List.of();
        }
        Trainee trainee = opt.get();
        return dao.getTraineeUnassignedTrainers(trainee);
    }

    public List<Trainer> updateTraineeTrainersList(String username,List<String> trainerUsernames) {
        Optional<Trainee> t = getTraineeProfile(username);
        if(t.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee: username={}", username);
            return List.of();
        }
        return List.copyOf(dao.updateTraineeTrainersList(t.get(),trainerUsernames));
    }

    public List<Training> getTraineeTrainings(String username) {
        Optional<Trainee> traineeOpt = dao.getTraineeByUsername(username);
        if(traineeOpt.isEmpty()) {
            logger.warn("Attempted to update non-existent trainee: username={}", username);
            return List.of();
        }
        Trainee trainee = traineeOpt.get();
        return trainee.getTrainings();
    }
}
