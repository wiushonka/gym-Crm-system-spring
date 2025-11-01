package com.example.storage.repos.trainer;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainer;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface TrainerRepository {
    void save(Trainer trainer);
    Optional<Trainer> findById(Long id);
    Optional<Trainer> findByUsername(String username);
    void updateTrainer(Long id,Trainer trainer);
    List<Trainer> findAllTrainers();
    Boolean isValidTrainer(String username, String password);
    void updatePassword(String username, String newPassword);
    void alterUserActivity(String username);
    List<Training> getTrainerTrainingsByUsername(String username);
    List<Training> getTrainerTrainingsByTraineeUsername(String traineeUsername,String trainerUsername);
    List<Training> getTrainerTrainingsByFromDate(Date cutOffDate, String username);
    List<Training> getTrainerTrainingsByToDate(Date cutOffDate , String username);
    List<Training> getTrainerTrainingsBetweenDates(Date fromDate, Date toDate, String username);
    List<Training> getAllTrainings(String username);
}