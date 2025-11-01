package com.example.dao;

import com.example.storage.entitys.training.Training;
import com.example.storage.repos.training.TrainingRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainingDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDAO.class);

    private TrainingRepository repo;

    public TrainingDAO() {}

    @Autowired
    public void setTrainingStorage(TrainingRepository repo) {
        this.repo = repo;
        logger.debug("TrainingStorage injected into TrainingDAO");
    }

    public void createTraining(@NotNull Training training) {
        repo.save(training);
        logger.debug("Training created: id={}", training.getId());
    }

    public Optional<Training> getTrainingById(Long id) {
        Optional<Training> optTraining=repo.findTrainingById(id);
        if(optTraining.isPresent()) {
            logger.debug("Retrieved training by ID: {}", id);
            return optTraining;
        }else{
            logger.debug("Training not found");
            return Optional.empty();
        }
    }

    public List<Training> findAllTrainings() {
        return repo.findAllTrainings();
    }

    public Optional<Training> getTrainingByName(String name) {
        return repo.findTrainingByName(name);
    }

    public void deleteTraining(@NotNull Training training) {
        logger.debug("deleting Training : id={}", training.getId());
        repo.delete(training);
    }
}
