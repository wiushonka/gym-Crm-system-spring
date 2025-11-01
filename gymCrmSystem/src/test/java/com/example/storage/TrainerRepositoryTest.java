package com.example.storage;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TrainerRepositoryTest {
    private final Logger logger = LoggerFactory.getLogger(TraineeRepositoryTest.class);

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

    @Test
    @Rollback
    public void testSaveTrainer() {
        User newTrainerUser = new User("new","new","new","new",true);
        Trainer newTrainer = new Trainer(newTrainerUser,type1);
        trainerRepo.save(newTrainer);
        Optional<Trainer> opt = trainerRepo.findById(newTrainer.getId());
        assertTrue(opt.isPresent());
        logger.info("Trainer saved, optional is not empty");
        Trainer tr = opt.get();
        assertEquals(newTrainer.getUserName(),tr.getUserName());
        assertEquals(newTrainer.getFirstName(),tr.getFirstName());
        assertEquals(newTrainer.getLastName(),tr.getLastName());
        assertEquals(newTrainer.getPassword(),tr.getPassword());
        assertEquals(newTrainer.getSpecialization().getId(),tr.getSpecialization().getId());
        assertTrue(newTrainer.isActive());
        assertEquals(type1.getTrainingTypeName(),opt.get().getSpecializationName());
        logger.info("retrieved information is correct");
    }

    @Test
    @Rollback
    public void testFindById() {
        Optional<Trainer> byUsername = trainerRepo.findByUsername("trainer1");
        assertTrue(byUsername.isPresent());
        trainer1 = byUsername.get();
        assertEquals("trainer1",trainer1.getUserName());
        Optional<Trainer> tr = trainerRepo.findById(trainer1.getId());
        assertTrue(tr.isPresent());
        Trainer trainer = tr.get();
        assertEquals(trainer1.getUserName(),trainer.getUserName());
        assertEquals(trainer1.getFirstName(),trainer.getFirstName());
        assertEquals(trainer1.getLastName(),trainer.getLastName());
        assertEquals(trainer1.getPassword(),trainer.getPassword());
        assertEquals(trainer1.getSpecializationName(),trainer.getSpecializationName());
        assertTrue(trainee1.isActive());
        logger.info("Find by id and find by username work as intended");
    }

    @Test
    @Rollback
    public void testTrainerStatusChange() {
        Trainer trainer = trainerRepo.findByUsername("trainer1").get();
        assertTrue(trainer.isActive());
        trainerRepo.alterUserActivity(trainer1.getUserName());
        Trainer alteredTrainer = trainerRepo.findByUsername("trainer1").get();
        assertFalse(alteredTrainer.isActive());
    }

    @Test
    @Rollback
    public void testTrainerUpdates() {
        assertTrue(trainerRepo.findByUsername("lilydavis").isPresent());
        Trainer trainer = trainerRepo.findByUsername("trainer1").get();
        User unamanaged = new User(trainer1.getFirstName(),trainer1.getLastName(),trainer1.getUserName(),trainer1.getPassword(),true);
        Trainer trUnmanaged = new Trainer(unamanaged,trainer1.getSpecialization());
        trainerRepo.updatePassword(trainer.getUserName(),"pass2");
        Trainer passUpdatedTrainer = trainerRepo.findById(trainer.getId()).get();
        assertEquals("pass2",passUpdatedTrainer.getPassword());
        assertEquals(trainer.getPassword(),passUpdatedTrainer.getPassword());
        assertNotEquals(trUnmanaged.getPassword(),passUpdatedTrainer.getPassword());
        logger.info("trainer update password works correctly");
        Trainer updatedTrainer = trainerRepo.findById(trainer.getId()).get();
        updatedTrainer.setSpecialization(type2);
        updatedTrainer.setFirstName("new first name");
        trainerRepo.updateTrainer(trainer.getId(),updatedTrainer);
        Trainer readUpdatedTrainer = trainerRepo.findById(trainer.getId()).get();
        assertEquals("new first name",readUpdatedTrainer.getFirstName());
        assertEquals(type2.getTrainingTypeName(),readUpdatedTrainer.getSpecializationName());
        logger.info("updates completed successfully");
    }

    @Test
    public void testQueries() {
        List<Training> trainingsByUsername = trainerRepo.getTrainerTrainingsByUsername("trainer1");
        assertEquals(1,trainingsByUsername.size());
        List<Training> trainer1TrainingsByTrainee2 = trainerRepo.getTrainerTrainingsByTraineeUsername("trainee2","trainer1");
        assertTrue(trainer1TrainingsByTrainee2.isEmpty());
        List<Training> trainerTrainingsFromLongTimeAgo = trainerRepo.getTrainerTrainingsByFromDate(new Date(),"trainer1");
        assertTrue(trainerTrainingsFromLongTimeAgo.isEmpty());
        List<Training> trainingsBetweenDates = trainerRepo.getTrainerTrainingsBetweenDates(new Date(),new Date(),"trainer1");
        assertTrue(trainingsBetweenDates.isEmpty());
    }

    @Test
    public void testTrainings() {
        Optional<Training> training = trainingRepo.findTrainingByName(training1.getTrainingName());
        assertTrue(training.isPresent());
        Optional<Training> nonExistingTraining = trainingRepo.findTrainingByName("bingus");
        assertTrue(nonExistingTraining.isEmpty());
    }
}
