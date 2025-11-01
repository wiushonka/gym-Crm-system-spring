package com.example.service;



import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.training.TrainingType;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import com.example.storage.entitys.users.User;
import com.example.storage.repos.trainee.TraineeRepository;
import com.example.storage.repos.trainer.TrainerRepository;
import com.example.storage.repos.training.TrainingRepository;
import com.example.storage.repos.trainingtype.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TrainingServiceTest {
    @Autowired
    private TraineeRepository traineeRepo;

    @Autowired
    private TrainerRepository trainerRepo;

    @Autowired
    private TrainingTypeRepository trainingTypeRepo;

    @Autowired
    private TrainingRepository trainingRepo;

    private Trainee trainee1;
    private Trainee trainee2;
    private Trainer trainer1;
    private Trainer trainer2;
    private TrainingType type1;
    private TrainingType type2;
    private Training training1;
    private Training training2;

    @BeforeEach
    void setUp() throws Exception {
        User trainee1user = new User("trainee1","trainee1","trainee1","pass1",true);
        User trainee2user = new User("trainee2","trainee2","trainee2","pass1",true);
        trainee1 = new Trainee(new Date(),"addr1",trainee1user);
        trainee2 = new Trainee(new Date(),"addr2",trainee2user);
        traineeRepo.save(trainee1);
        traineeRepo.save(trainee2);


        type1 = new TrainingType("Training Type 1");
        type2 = new TrainingType("Training Type 2");
        trainingTypeRepo.save(type1);
        trainingTypeRepo.save(type2);

        User trainer1user = new User("trainer1","trainer1","trainer1","pass1",true);
        User trainer2user = new User("trainer2","trainer2","trainer2","pass1",true);
        trainer1 = new Trainer(trainer1user,type1);
        trainer2 = new Trainer(trainer2user,type2);
        trainerRepo.save(trainer1);
        trainerRepo.save(trainer2);

        training1 = new Training("training name 1", new Date(),0,trainee1,type1,trainer1);
        training2 = new Training("training name 2", new Date(),1,trainee2,type2,trainer2);
        traineeRepo.addTrainerToTrainee(trainer1,"trainee1");
        traineeRepo.addTrainerToTrainee(trainer2,"trainee2");
        trainingRepo.save(training1);
        trainingRepo.save(training2);
    }

    @Autowired
    TrainingService service;

    @Test
    public void test1() {
        List<Training> allTrainings = service.getTrainings();
        assertEquals(2, allTrainings.size());

        Training trainingByName = service.getTrainingByName(allTrainings.getFirst().getTrainingName()).get();
        Training trainingById = service.getTrainingById(trainingByName.getId()).get();
        assertEquals(trainingByName.getId(), trainingById.getId());
        assertEquals(trainingByName.getTrainingName(), trainingById.getTrainingName());
        assertEquals(trainingByName.getTrainingType().getId(), trainingById.getTrainingType().getId());
    }
}
