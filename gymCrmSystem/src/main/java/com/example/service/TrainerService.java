package com.example.service;

import com.example.dao.TrainerDAO;
import com.example.dao.TrainingTypeDAO;
import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.training.TrainingType;
import com.example.storage.entitys.users.Trainer;
import com.example.utili.Utili;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    protected PasswordEncoder encoder; // I made these protected just to use them in tests easily
    private TrainerDAO dao;
    private TrainingTypeDAO typeDao;

    @Autowired
    public void setTrainerDao(TrainerDAO dao){
        this.dao = dao;
        logger.debug("TrainerDAO injected into TrainerService");
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder encoder){
        this.encoder = encoder;
    }

    @Autowired
    public void setTypeDao(TrainingTypeDAO typeDao){
        this.typeDao = typeDao;
        logger.debug("trainingTypeDao injected into TrainerService");
    }

    public void createTrainerProfileWithSpecialization(@NotNull Trainer trainer,@NotNull String specializationName) {
        Optional<TrainingType> spec = typeDao.findByName(specializationName);
        if (spec.isEmpty()){
            logger.error("can not add user/trainer with non existent specialization name={}", specializationName);
            throw new IllegalArgumentException("spezialisationName is incorrect");
        }

        TrainingType specialization = spec.get();
        trainer.setSpecialization(specialization);
        createTrainerProfile(trainer);
    }

    private void createTrainerProfile(@NotNull Trainer trainer) {
        String firstName = trainer.getFirstName();
        String lastName = trainer.getLastName();
        firstName=firstName.toLowerCase();
        lastName=lastName.toLowerCase();
        String password = Utili.generatePassword();
        String username = Utili.generateUsername(firstName, lastName,
                                    genName->dao.getTrainerByUsername(genName).isPresent());

        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUserName(username);
        trainer.setPassword(encoder.encode(password));
        trainer.setActive(true);
        dao.addTrainer(trainer);
        logger.info("Created trainer profile: username={}, firstName={}, lastName={}", username, firstName, lastName);
    }

    public void updateTrainerProfileWithSpecialization(@NotNull Long id,@NotNull Trainer trainer,@NotNull String specializationName) {
        Optional<TrainingType> spec = typeDao.findByName(specializationName);
        if(spec.isEmpty()) {
            logger.error("provided specialisation name does not exist in database ...");
            throw new IllegalArgumentException("specializationName is incorrect");
        }
        TrainingType specialization = spec.get();
        trainer.setSpecialization(specialization);
        updateTrainerProfileById(id,trainer);
    }

    private void updateTrainerProfileById(@NotNull Long id, @NotNull Trainer trainer){
        Optional<Trainer> oldOpt = dao.getTrainerById(id);
        if (oldOpt.isEmpty()) {
            logger.warn("Attempted to update non-existent trainer by ID: id={}", id);
            return;
        }
        dao.updateTrainer(id, trainer);
        logger.info("Updated trainer profile by ID: id={}, username={}", id, trainer.getUserName());
    }

    public Optional<Trainer> getTrainerProfile(String username){
        Optional<Trainer> trainerOpt = dao.getTrainerByUsername(username);
        if (trainerOpt.isPresent()) {
            logger.debug("Retrieved trainer profile: username={}", username);
            return trainerOpt;
        } else {
            logger.warn("No trainer found with username={}", username);
            return Optional.empty();
        }
    }

    public List<Trainer> getAllTrainers(){
        List<Trainer> all = dao.getAllTrainers();
        logger.debug("Retrieved all trainers, count={}", all.size());
        return all;
    }

    public Boolean isValidTrainer(String username, String password){
        return dao.isValidTrainer(username, password);
    }

    public void updatePassword(String username, String newPassword){ dao.updatePassword(username,encoder.encode(newPassword)); }

    public void alterUserActivity(String username){
        dao.alterUserActivity(username);
    }

    public List<Training> getTrainerTrainingsByUsername(String username){
        return dao.getTrainerTrainingsByUsername(username);
    }

    public List<Training> getTrainerTrainingsByTraineeUsername(String traineeUsername,String trainerUsername){
        return dao.getTrainerTrainingsByTraineeUsername(traineeUsername,trainerUsername);
    }

    public List<Training> getTrainerTrainingsByFromDate(Date cutOffDate, String username){
        return dao.getTrainerTrainingsByFromDate(cutOffDate,username);
    }

    public List<Training> getTrainerTrainingsByToDate(Date cutOffDate , String username){
        return dao.getTrainerTrainingsByToDate(cutOffDate,username);
    }

    public List<Training> getTrainerTrainingsBetweenDates(Date fromDate, Date toDate, String username){
        return dao.getTrainerTrainingsBetweenDates(fromDate,toDate,username);
    }

    public List<Training> getAllTrainings(String username) {
        return dao.getAllTrainings(username);
    }
}
