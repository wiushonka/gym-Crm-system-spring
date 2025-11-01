package com.example.facade;


import com.example.service.TraineeService;
import com.example.service.TrainerService;
import com.example.service.TrainingService;
import com.example.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class GymFacade {

    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    @Autowired
    public GymFacade(TrainerService traineService, TraineeService traineeService,
                     TrainingService trainingService, TrainingTypeService trainingTypeService) {
        this.trainerService = traineService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    public TrainingService getTrainingService() { return trainingService; }

    public TrainerService getTrainerService() { return trainerService; }

    public TraineeService getTraineeService() { return traineeService; }

    public TrainingTypeService getTrainingTypeService() { return trainingTypeService; }
}
