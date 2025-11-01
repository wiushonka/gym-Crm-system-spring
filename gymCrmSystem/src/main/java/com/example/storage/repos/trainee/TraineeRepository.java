package com.example.storage.repos.trainee;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    void save(Trainee trainee);
    Optional<Trainee> findById(Long id);
    Optional<Trainee> findByUsername(String username);
    void alterUserActivity(String username);
    Boolean isValidTrainee(String username,String password);
    void updatePassword(String username,String password);
    void updateTrainee(Long id,Trainee trainee);
    List<Trainee> findAllTrainees();
    void deleteTrainee(String username);
    void deleteTrainee(Trainee trainee);
    void addTrainerToTrainee(Trainer trainer, String traineeUsername);
    void removeTrainerFromTrainee(Trainer trainer, String traineeUsername);
    List<Training> getTraineeTrainingsByTraineeUsername(String username);
    List<Training> getTraineeTrainingsByTrainerUsername(String traineeUsername, String trainerUsername);
    List<Training> getTraineeTrainingsByFromDate(Date cutOffDate, String username);
    List<Training> getTraineeTrainingsByToDate(Date cutOffDate, String username);
    List<Training> getTraineeTrainingsBetweenDates(Date fromDate, Date toDate, String username);
    List<Trainer> getTraineeUnassignedTrainers(Trainee trainee);
}
