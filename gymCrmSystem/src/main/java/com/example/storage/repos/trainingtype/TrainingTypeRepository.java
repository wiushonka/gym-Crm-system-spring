package com.example.storage.repos.trainingtype;

import com.example.storage.entitys.training.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    void save(TrainingType trainingType);
    Optional<TrainingType> findByName(String trainingTypeName);
    List<TrainingType> findAllTrainingTypes();
}

