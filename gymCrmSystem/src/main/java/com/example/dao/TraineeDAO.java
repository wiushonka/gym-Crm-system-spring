package com.example.dao;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import com.example.storage.repos.trainee.TraineeRepository;
import com.example.storage.repos.trainer.TrainerRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TraineeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDAO.class);

    private TraineeRepository traineeRepo;

    private TrainerRepository trainerRepo;

    public TraineeDAO() {}

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepo) {
        this.traineeRepo = traineeRepo;
        logger.debug("TraineeStorage injected into TraineeDAO");
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    public void addTrainee(@NotNull Trainee trainee) {
        traineeRepo.save(trainee);
        logger.info("Added Trainee: id={}, username={}", trainee.getId(), trainee.getUserName());
    }

    public void removeTraineeById(Long id) {
        Optional<Trainee> trainee = traineeRepo.findById(id);
        if (trainee.isEmpty()) {
            logger.debug("Can not remove nonexistent Trainee: id={}", id);
            return;
        }
        traineeRepo.deleteTrainee(trainee.get());
        logger.debug("Removed Trainee: id={}", id);
    }

    public void updateTrainee(Long id, @NotNull Trainee trainee) {
        traineeRepo.updateTrainee(id,trainee);
        logger.info("Updated Trainee: id={}, username={}", id, trainee.getUserName());
    }

    public Optional<Trainee> getTraineeById(Long id) {
        Optional<Trainee> opTrainee=traineeRepo.findById(id);
        if(opTrainee.isPresent()) {
            Trainee trainee = opTrainee.get();
            logger.info("Retrieved Trainee: id={}, username={}", id, trainee.getUserName());
            return Optional.of(trainee);
        }else{
            logger.info("Trainee with that id could not be found");
            return Optional.empty();
        }
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        Optional<Trainee> opTrainee=traineeRepo.findByUsername(username);
        if(opTrainee.isPresent()) {
            Trainee trainee = opTrainee.get();
            logger.info("Retrieved Trainee: username={}", username);
            return Optional.of(trainee);
        }else{
            logger.info("Trainee with that username could not be found");
            return Optional.empty();
        }
    }

    public List<Trainee> getAllTrainees() {
        List<Trainee> trainees = traineeRepo.findAllTrainees();
        logger.info("Retrieving all trainees, count={}", trainees.size());
        return trainees;
    }

    public void alterUserActivity(@NotNull Trainee trainee){
        traineeRepo.alterUserActivity(trainee.getUserName());
    }

    public Boolean isValidTrainee(@NotNull Trainee trainee) {
        return traineeRepo.isValidTrainee(trainee.getUserName(),trainee.getPassword());
    }

    public void updateTraineePassword(@NotNull Trainee trainee, String newPassword) {
        traineeRepo.updatePassword(trainee.getUserName(),newPassword);
    }

    public List<Training> getTraineeTrainingsByTraineeUsername(String username) {
        return traineeRepo.getTraineeTrainingsByTraineeUsername(username);
    }

    public List<Training> getTraineeTrainingsByTrainerUsername(String traineeUsername, String trainerUsername){
        return traineeRepo.getTraineeTrainingsByTrainerUsername(traineeUsername,trainerUsername);
    }

    public List<Training> getTraineeTrainingsByFromDate(Date cutOffDate, String username){
        return traineeRepo.getTraineeTrainingsByFromDate(cutOffDate,username);
    }

    public List<Training> getTraineeTrainingsByToDate(Date cutOffDate, String username){
        return traineeRepo.getTraineeTrainingsByToDate(cutOffDate, username);
    }
    public List<Training> getTraineeTrainingsBetweenDates(Date fromDate, Date toDate, String username){
        return traineeRepo.getTraineeTrainingsBetweenDates(fromDate,toDate,username);
    }

    /*
    As I got from explanation it should return all trainings,
    that are not assign to this particular trainee.
    */
    public List<Trainer> getTraineeUnassignedTrainers(Trainee trainee){
        return traineeRepo.getTraineeUnassignedTrainers(trainee);
    }
    public void addTrainerToTrainee(Trainer trainer, String traineeUsername){
        traineeRepo.addTrainerToTrainee(trainer,traineeUsername);
    }
    public void removeTrainerFromTrainee(Trainer trainer, String traineeUsername){
        traineeRepo.removeTrainerFromTrainee(trainer,traineeUsername);
    }

    public List<Trainer> updateTraineeTrainersList(@NotNull Trainee trainee, List<String> trainerUsernames) {

        trainee.getTrainers().clear();

        logger.info("removed trainers");

        for(String trainerUsername:trainerUsernames){
            Optional<Trainer> t = trainerRepo.findByUsername(trainerUsername);
            if(t.isEmpty()){
                logger.warn("Trainer username in updateTraineeTrainersList does not match any present trainers >:");
                continue;
            }
            Trainer trainer = t.get();
            traineeRepo.addTrainerToTrainee(trainer,trainee.getUserName());
        }

        logger.info("Updated trainee trainers, count={}", trainee.getTrainers().size());
        return trainee.getTrainers();
    }
}
