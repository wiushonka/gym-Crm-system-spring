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
@Transactional // roll back before each on each testcase so it does not throw unique constraint violation when reinserting stuff
public class TraineeRepositoryTest {

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
    public void TestSave(){
        User newTraineeUser = new User("new","new","new","new",true);
        Trainee newTrainee = new Trainee(new Date(),"new",newTraineeUser);
        traineeRepo.save(newTrainee);
        Optional<Trainee> opt = traineeRepo.findById(newTrainee.getId());
        assertTrue(opt.isPresent());
        logger.info("Trainee saved, optional is not empty");
        Trainee tr = opt.get();
        assertEquals(newTrainee.getUserName(),tr.getUserName());
        assertEquals(newTrainee.getFirstName(),tr.getFirstName());
        assertEquals(newTrainee.getLastName(),tr.getLastName());
        assertEquals(newTrainee.getPassword(),tr.getPassword());
        assertEquals(newTrainee.getAddress(),tr.getAddress());
        assertTrue(newTrainee.isActive());
        assertEquals("new",opt.get().getAddress());
        logger.info("retrieved information is correct");
    }

    @Test
    @Rollback
    public void testFindById() {
        Optional<Trainee> byUsername = traineeRepo.findByUsername("trainee1");
        assertTrue(byUsername.isPresent());
        trainee1 = byUsername.get();
        assertEquals("trainee1",trainee1.getUserName());
        Optional<Trainee> tr = traineeRepo.findById(trainee1.getId());
        assertTrue(tr.isPresent());
        Trainee trainee = tr.get();
        assertEquals(trainee1.getUserName(),trainee.getUserName());
        assertEquals(trainee1.getFirstName(),trainee.getFirstName());
        assertEquals(trainee1.getLastName(),trainee.getLastName());
        assertEquals(trainee1.getPassword(),trainee.getPassword());
        assertEquals(trainee1.getAddress(),trainee.getAddress());
        assertTrue(trainee1.isActive());
        logger.info("Find by id and find by username work as intended");
    }

    @Test
    @Rollback
    public void testTraineeStatusChange() {
        Trainee trainee = traineeRepo.findByUsername("trainee1").get();
        assertTrue(trainee.isActive());
        traineeRepo.alterUserActivity(trainee1.getUserName());
        Trainee alteredTrainee = traineeRepo.findByUsername("trainee1").get();
        assertFalse(alteredTrainee.isActive());
    }

    @Test
    @Rollback
    public void testTraineeUpdates() {
        assertTrue(traineeRepo.findByUsername("johndoe").isPresent());
        Trainee trainee = traineeRepo.findByUsername("trainee1").get();
        User unamanaged = new User(trainee1.getFirstName(),trainee1.getLastName(),trainee1.getPassword(),trainee1.getAddress(),true);
        Trainee trUnmanaged = new Trainee(trainee.getDateOfBirth(),trainee.getAddress(),unamanaged);
        traineeRepo.updatePassword(trainee.getUserName(),"pass2");
        Trainee passUpdatedTrainee = traineeRepo.findById(trainee.getId()).get();
        assertEquals("pass2",passUpdatedTrainee.getPassword());
        assertEquals(trainee.getPassword(),passUpdatedTrainee.getPassword());
        assertNotEquals(trUnmanaged.getPassword(),passUpdatedTrainee.getPassword());

        logger.info("trainee update password works correctly");

        Trainee updatedTrainee = traineeRepo.findById(trainee.getId()).get();
        Date date=new Date();
        updatedTrainee.setDateOfBirth(date);
        updatedTrainee.setAddress("new address");
        updatedTrainee.setFirstName("new first name");
        traineeRepo.updateTrainee(trainee.getId(),updatedTrainee);

        Trainee readUpdatedTrainee = traineeRepo.findById(trainee.getId()).get();
        assertEquals("new first name",readUpdatedTrainee.getFirstName());
        assertEquals("new address",readUpdatedTrainee.getAddress());
        assertEquals(date,readUpdatedTrainee.getDateOfBirth());
        logger.info("updates completed successfully");
    }

    @Test
    public void testIfCommandLineRunnerIsLoadingCorrectly() {
        assertTrue(traineeRepo.findByUsername("johndoe").isPresent());
        assertTrue(traineeRepo.findByUsername("janesmith").isPresent());
        assertTrue(traineeRepo.findByUsername("mikejohnson").isPresent());
        assertTrue(traineeRepo.findByUsername("sarahw").isPresent());
        assertTrue(traineeRepo.findByUsername("davidb").isPresent());
        assertEquals(7,traineeRepo.findAllTrainees().size()); // 5 from clr 2 from @BeforEach
    }

    @Test
    @Rollback
    public void testDeleteTraineeWithUsername() {
        Trainer trainerBeforeDelete = trainerRepo.findByUsername("trainer1").get();
        assertEquals(1,trainerBeforeDelete.getTrainees().size());
        List<Training> trainings = trainingRepo.findAllTrainings();
        assertEquals(2, trainings.size());
        traineeRepo.deleteTrainee("trainee1");
        List<Training> afterDeleteTrainings = trainingRepo.findAllTrainings();
        assertEquals(1, afterDeleteTrainings.size());
        assertEquals(trainee2.getUserName(),afterDeleteTrainings.getFirst().getTrainee().getUserName());
        assertEquals(trainer2.getUserName(),afterDeleteTrainings.getFirst().getTrainer().getUserName());
        Trainer trainerAfterDelete = trainerRepo.findByUsername("trainer1").get();
        assertEquals(0,trainerAfterDelete.getTrainees().size());
    }
    @Test
    @Rollback
    public void testDeleteTraineeWithObjectTrainee() {
        Trainer trainerBeforeDelete = trainerRepo.findByUsername("trainer1").get();
        assertEquals(1,trainerBeforeDelete.getTrainees().size());
        List<Training> trainings = trainingRepo.findAllTrainings();
        assertEquals(2, trainings.size());
        Trainee trainee = traineeRepo.findByUsername("trainee1").get();
        traineeRepo.deleteTrainee(trainee);
        List<Training> afterDeleteTrainings = trainingRepo.findAllTrainings();
        assertEquals(1, afterDeleteTrainings.size());
        assertEquals(trainee2.getUserName(),afterDeleteTrainings.getFirst().getTrainee().getUserName());
        assertEquals(trainer2.getUserName(),afterDeleteTrainings.getFirst().getTrainer().getUserName());
        Trainer trainerAfterDelete = trainerRepo.findByUsername("trainer1").get();
        assertEquals(0,trainerAfterDelete.getTrainees().size());
    }

    @Test
    @Rollback
    public void testTrainerAdditionAndRemovalFromTrainee() {
        Trainer trainerBeforeAdd = trainerRepo.findByUsername("trainer2").get();
        assertEquals(1,trainerBeforeAdd.getTrainees().size());
        traineeRepo.addTrainerToTrainee(trainer2,"trainee1");
        Trainer trainerAfterAdd = trainerRepo.findByUsername("trainer2").get();
        assertEquals(2,trainerAfterAdd.getTrainees().size());
        Trainee traineeAfterAdd = traineeRepo.findByUsername("trainee1").get();
        assertEquals(2,traineeAfterAdd.getTrainers().size());
        traineeRepo.removeTrainerFromTrainee(trainer2,"trainee1");
        Trainer trainerAfterRemove = trainerRepo.findByUsername("trainer2").get();
        assertEquals(1,trainerAfterRemove.getTrainees().size());
        Trainee traineeAfterRemove = traineeRepo.findByUsername("trainee1").get();
        assertEquals(1,traineeAfterRemove.getTrainers().size());
    }

    @Test
    public void testQueries(){
        List<Training> trainingsByUsername = traineeRepo.getTraineeTrainingsByTraineeUsername("trainee1");
        assertEquals(1,trainingsByUsername.size());
        List<Training> trainee1TrainingsByTrainer2 = traineeRepo.getTraineeTrainingsByTrainerUsername("trainee1","trainer2");
        assertTrue(trainee1TrainingsByTrainer2.isEmpty());
        List<Training> traineeTrainingsFromLongTimeAgo = traineeRepo.getTraineeTrainingsByFromDate(new Date(),"trainee1");
        assertTrue(traineeTrainingsFromLongTimeAgo.isEmpty());
        List<Training> trainingsBetweenDates = traineeRepo.getTraineeTrainingsBetweenDates(new Date(),new Date(),"trainee1");
        assertTrue(trainingsBetweenDates.isEmpty());
        Trainee trainee = traineeRepo.findByUsername("trainee1").get();
        List<Trainer> trainee1UnassignedTrainers = traineeRepo.getTraineeUnassignedTrainers(trainee);
        assertEquals(6,trainee1UnassignedTrainers.size());
    }
}
