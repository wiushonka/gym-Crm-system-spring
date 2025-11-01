package com.example.dao;

import com.example.storage.entitys.training.TrainingType;
import com.example.storage.repos.trainingtype.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrainingTypeDAO {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeDAO.class);

    private TrainingTypeRepository repo;

    public TrainingTypeDAO() {}

    @Autowired
    public void setTrainingStorage(TrainingTypeRepository repo) {
        this.repo = repo;
        logger.debug("TrainingStorage injected into TrainingDAO");
    }

    public void save(TrainingType trainingType){
        repo.save(trainingType);
    }

    public Optional<TrainingType> findByName(String trainingTypeName){
        return repo.findByName(trainingTypeName);
    }

    public List<TrainingType> findAllTrainingTypes() {
        return repo.findAllTrainingTypes();
    }
}
