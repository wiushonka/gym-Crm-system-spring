package com.example.service;

import com.example.dao.TraineeDAO;
import com.example.dao.TrainingDAO;
import com.example.storage.entitys.training.Training;
import com.example.utili.Utili;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDAO dao;

    private TraineeDAO traineeDAO;

    @Autowired
    public void setTrainingDao(TrainingDAO dao) {
        this.dao = dao;
        logger.info("TrainingDAO injected into TrainingService");
    }

    @Autowired
    public void setTraineeDAO(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
        logger.info("TraineeDAO injected into TrainingService");
    }

    public List<Training> getTrainings() {
        List<Training> trainings = dao.findAllTrainings();
        logger.debug("Retrieved all trainings, count={}", trainings.size());
        return trainings;
    }

    public void addTraining(@NotNull Training training) {
        String name=training.getTrainingName().toLowerCase();
        name=Utili.generateUniqueNameForTraining(name,genName->dao.getTrainingByName(genName).isPresent());
        training.setTrainingName(name);
        dao.createTraining(training);
        traineeDAO.addTrainerToTrainee(training.getTrainer(),training.getTrainee().getUserName());
        logger.info("Added new training: id={}, name={}, type={}", training.getId(),
                        training.getTrainingName(),training.getTrainingType());
    }

    public Optional<Training> getTrainingById(Long id) {
        Optional<Training> t = dao.getTrainingById(id);
        if (t.isPresent()) {
            logger.debug("Retrieved training by id={}: name={}", id, t.get().getTrainingName());
            return t;
        } else {
            logger.warn("Training with id={} not found", id);
            return Optional.empty();
        }
    }

    public Optional<Training> getTrainingByName(String name) {
        Optional<Training> t = dao.getTrainingByName(name);
        if (t.isPresent()) {
            logger.debug("Retrieved training by name={}: id={}", name, t.get().getId());
            return t;
        } else {
            logger.warn("Training with name={} not found", name);
            return Optional.empty();
        }
    }

    public void deleteTraining(String trainingName) {
        Optional<Training> training = dao.getTrainingByName(trainingName);
        if (training.isPresent()) {
            dao.deleteTraining(training.get());
        }else{
            logger.debug("Training with name={} not found", trainingName);
        }
    }
}
