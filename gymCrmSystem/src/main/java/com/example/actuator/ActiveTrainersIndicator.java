package com.example.actuator;

import com.example.service.TrainerService;
import com.example.storage.entitys.users.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActiveTrainersIndicator implements HealthIndicator {

    private TrainerService trainerService;

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Override
    public Health health() {
        List<String> activeTrainers = trainerService.getAllTrainers().stream().filter(Trainer::isActive).map(Trainer::getUserName).toList();
        if(!activeTrainers.isEmpty()){
            return Health.up().withDetail("activeTrainers", activeTrainers).build();
        }else{
            return Health.down().withDetail("activeTrainers", "Currently there are no active trainers").build();
        }
    }
}
