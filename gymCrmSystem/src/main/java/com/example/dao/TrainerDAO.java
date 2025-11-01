package com.example.dao;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainer;
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
public class TrainerDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDAO.class);

    private TrainerRepository repo;

    public TrainerDAO() {}

    @Autowired
    public void setTrainerStorage(TrainerRepository repo) {
        this.repo = repo;
        logger.info("TrainerStorage injected into TrainerDAO");
    }

    public void addTrainer(@NotNull Trainer trainer) {
        repo.save(trainer);
        logger.info("Added Trainer: id={}, username={}", trainer.getId(), trainer.getUserName());
    }

    public void updateTrainer(Long id, @NotNull Trainer trainer) {
        repo.updateTrainer(id,trainer);
        logger.info("Updated Trainer: id={}, username={}", id, trainer.getUserName());
    }

    public Optional<Trainer> getTrainerById(Long id) {
        Optional<Trainer> opTrainer=repo.findById(id);
        if(opTrainer.isPresent()) {
            Trainer trainer = opTrainer.get();
            logger.info("Retrieved Trainer: id={}, username={}", id, trainer.getUserName());
            return Optional.of(trainer);
        }else{
            logger.info("Trainer with that id could not be found");
            return Optional.empty();
        }
    }

    public Optional<Trainer> getTrainerByUsername(String username) {
        Optional<Trainer> opTrainer=repo.findByUsername(username);
        if(opTrainer.isPresent()) {
            Trainer trainer = opTrainer.get();
            logger.info("Retrieved Trainer: username={}", username);
            return Optional.of(trainer);
        }else{
            logger.info("Trainer with that username could not be found");
            return Optional.empty();
        }
    }

    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = repo.findAllTrainers();
        logger.info("Retrieving all trainers, count={}", trainers.size());
        return trainers;
    }

    public Boolean isValidTrainer(String username, String password){
        return repo.isValidTrainer(username, password);
    }

    public void updatePassword(String username, String newPassword){
        repo.updatePassword(username,newPassword);
    }

    public void alterUserActivity(String username){
        repo.alterUserActivity(username);
    }

    public List<Training> getTrainerTrainingsByUsername(String username){
        return repo.getTrainerTrainingsByUsername(username);
    }

    public List<Training> getTrainerTrainingsByTraineeUsername(String traineeUsername,String trainerUsername){
        return repo.getTrainerTrainingsByTraineeUsername(traineeUsername,trainerUsername);
    }

    public List<Training> getTrainerTrainingsByFromDate(Date cutOffDate, String username){
        return repo.getTrainerTrainingsByFromDate(cutOffDate,username);
    }

    public List<Training> getTrainerTrainingsByToDate(Date cutOffDate , String username){
        return repo.getTrainerTrainingsByToDate(cutOffDate,username);
    }

    public List<Training> getTrainerTrainingsBetweenDates(Date fromDate, Date toDate, String username){
        return repo.getTrainerTrainingsBetweenDates(fromDate,toDate,username);
    }

    public List<Training> getAllTrainings(String username) {
        return repo.getAllTrainings(username);
    }
}
