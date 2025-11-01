package com.example.storage.repos.training;

import com.example.storage.entitys.training.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    void save(Training training);
    List<Training> findAllTrainings();
    Optional<Training> findTrainingById(Long id);
    Optional<Training> findTrainingByName(String name);
    void delete(Training training);
}
