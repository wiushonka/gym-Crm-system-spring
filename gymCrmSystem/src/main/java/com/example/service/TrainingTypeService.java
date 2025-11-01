package com.example.service;

import com.example.dao.TrainingTypeDAO;
import com.example.storage.entitys.training.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingTypeService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeService.class);

    private TrainingTypeDAO dao;

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDAO dao) {
        this.dao = dao;
        logger.info("TrainingDAO injected into TrainingService");
    }

    public void save(TrainingType trainingType){
        dao.save(trainingType);
    }

    public Optional<TrainingType> findByName(String trainingTypeName){
        return dao.findByName(trainingTypeName);
    }

    public List<TrainingType> findAll(){
        return dao.findAllTrainingTypes();
    }
}
