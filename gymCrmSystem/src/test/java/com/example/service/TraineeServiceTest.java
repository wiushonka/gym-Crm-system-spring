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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TraineeServiceTest {

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
    private TraineeService service;

    @Test
    @Rollback
    public void testCreateDeleteTraineeProfile(){
        int x=service.getAllTrainees().size();
        User trainee1user = new User("trainee1","trainee1","trainee1","pass1",true);
        Trainee trainee1 = new Trainee(new Date(),"addr1",trainee1user);
        service.createTraineeProfile(trainee1);
        assertEquals(x+1,service.getAllTrainees().size());
        service.createTraineeProfile(trainee1);
        assertEquals(x+1,service.getAllTrainees().size());
        service.deleteTraineeProfile(trainee1.getUserName());
        assertEquals(x,service.getAllTrainees().size());
    }

    @Test
    @Rollback
    public void updateTraineeProfile() {
        Optional<Trainee> trainee = service.getTraineeProfile("johndoe");
        assertTrue(trainee.isPresent());
        Trainee trainee11 = trainee.get();
        trainee11.setFirstName("bondoe");
        trainee11.setLastName("dodoe");
        service.updateTraineeProfile(trainee1);
        Trainee updatedTrainee = service.getTraineeProfile("johndoe").get();
        assertEquals(updatedTrainee.getFirstName(),trainee11.getFirstName());
        assertEquals(updatedTrainee.getLastName(),trainee11.getLastName());
        service.updatePassword(updatedTrainee,"newPasss");
        Trainee uupdatedTrainee = service.getTraineeProfile("johndoe").get();
        assertTrue(service.encoder.matches("newPasss",uupdatedTrainee.getPassword()));
    }

    @Test
    @Rollback
    public void testUpdateTraineeProfileWithId() {
        Optional<Trainee> trainee = service.getTraineeProfile("johndoe");
        assertTrue(trainee.isPresent());
        Trainee trainee11= trainee.get();
        trainee11.setFirstName("bondoe");
        trainee11.setLastName("dodoe");
        service.updateTraineeProfileById(trainee1.getId(),trainee11);
        Trainee updatedTrainee = service.getTraineeProfile("johndoe").get();
        assertEquals(updatedTrainee.getFirstName(),trainee11.getFirstName());
        assertEquals(updatedTrainee.getLastName(),trainee11.getLastName());
    }

    @Test
    @Rollback
    public void testUpdateTraineeProfileWithUserName() {
        Optional<Trainee> trainee = service.getTraineeProfile("johndoe");
        assertTrue(trainee.isPresent());
        Trainee trainee11 = trainee.get();
        trainee11.setFirstName("bondoe");
        trainee11.setLastName("dodoe");
        service.updateTraineeProfileByUsername(trainee1.getUserName(),trainee11);
        Trainee updatedTrainee = service.getTraineeProfile("johndoe").get();
        assertEquals(updatedTrainee.getFirstName(),trainee11.getFirstName());
        assertEquals(updatedTrainee.getLastName(),trainee11.getLastName());
    }

    @Test
    @Rollback
    public void testAlterUserActivity() {
        List<Trainee> trainees = service.getAllTrainees();
        assertFalse(trainees.isEmpty());
        for(Trainee trainee : trainees) {
            assertTrue(trainee.isActive());
            service.alterUserActivity(trainee);
        }
        List<Trainee> alteredTrainees = service.getAllTrainees();
        assertFalse(alteredTrainees.isEmpty());
        for(Trainee trainee : alteredTrainees) {
            assertFalse(trainee.isActive());
        }
    }

    @Test
    @Rollback
    public void testAlterUserActivityByUsername() {
        List<Trainee> trainees = service.getAllTrainees();
        assertFalse(trainees.isEmpty());
        for(Trainee trainee : trainees) {
            assertTrue(trainee.isActive());
            service.alterUserActivityByUsername(trainee.getUserName());
        }
        List<Trainee> alteredTrainees = service.getAllTrainees();
        assertFalse(alteredTrainees.isEmpty());
        for(Trainee trainee : alteredTrainees) {
            assertFalse(trainee.isActive());
        }
    }

    @Test
    public void checkValidity() {
        assertTrue(service.checkIfValidUsernameAndPassword("johndoe","pass123"));
        assertFalse(service.checkIfValidUsernameAndPassword("johndoe","bangus"));
        Trainee trainee = service.getTraineeProfile("johndoe").get();
        assertTrue(service.checkIfValidTrainee(trainee));
        Trainee trainee11 = new Trainee();
        assertFalse(service.checkIfValidTrainee(trainee11));
    }

    @Test
    @Rollback
    public void testQueries() {
        List<Training> trainings = service.getTraineeTrainings("trainee1");
        assertFalse(trainings.isEmpty());
        List<Training> trainee1Trainer2Trainings = service.getTraineeTrainingsByTrainerUsername("trainer1", "trainee2");
        assertTrue(trainee1Trainer2Trainings.isEmpty());
        List<Training> trainingsFromDate = service.getTraineeTrainingsByFromDate(new Date(),"trainee1");
        assertTrue(trainingsFromDate.isEmpty());
        List<Training> trainingsToDate = service.getTraineeTrainingsByToDate(new Date(),"trainee1");
        assertFalse(trainingsToDate.isEmpty());
        List<Training> trainingsBet = service.getTraineeTrainingsBetweenDates(new Date(),new Date(),"trainee1");
        assertTrue(trainingsBet.isEmpty());
        List<Trainer> unassignedTrainers = service.getTraineeUnassignedTrainers("trainee1");
        assertEquals(6, unassignedTrainers.size());
        List<String> names = new ArrayList<>(); names.add("trainer1"); names.add("trainer2");
        List<Trainer> updated = service.updateTraineeTrainersList("trainee1",names);
        unassignedTrainers = service.getTraineeUnassignedTrainers("trainee1");
        assertEquals(5, unassignedTrainers.size());

    }
}
