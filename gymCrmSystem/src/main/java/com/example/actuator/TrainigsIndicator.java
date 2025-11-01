package com.example.actuator;

import com.example.service.TrainingService;
import com.example.storage.entitys.training.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainigsIndicator implements HealthIndicator {

    private TrainingService trainingService;

    @Autowired
    public void setTrainingService(TrainingService trainingService){
        this.trainingService = trainingService;
    }

    @Override
    public Health health() {
        List<String> totalTrainings = trainingService.getTrainings().stream().map(Training::getTrainingName).toList();
        if(!totalTrainings.isEmpty()){
            return Health.up().withDetail("trainings",totalTrainings).build();
        }else{
            return Health.down().withDetail("trainings","no trainings available").build();
        }
    }
}
