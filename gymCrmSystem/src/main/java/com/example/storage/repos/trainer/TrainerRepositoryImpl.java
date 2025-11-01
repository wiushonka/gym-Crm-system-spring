package com.example.storage.repos.trainer;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
@Transactional
public class TrainerRepositoryImpl implements TrainerRepository {

    private static final Logger logger = Logger.getLogger(TrainerRepositoryImpl.class.getName());

    private final EntityManager em;

    @Autowired
    public TrainerRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Trainer trainer) {
        logger.info("Storing trainer " + trainer.getUserName());
        em.persist(trainer);
        logger.info("Stored trainer " + trainer.getUserName());
    }

    @Override
    public Boolean isValidTrainer(String username, String password) {
        Optional<Trainer> trainer = findByUsername(username);
        if (trainer.isEmpty()) return false;
        return trainer.get().getPassword().equals(password);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        TypedQuery<Trainer> tq = em.createQuery("select t from Trainer t " +
                        "where t.user.userName=:username",
                Trainer.class);
        tq.setParameter("username", username);
        List<Trainer> list = tq.setMaxResults(1).getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        Optional<Trainer> trainerOpt = findByUsername(username);
        if (trainerOpt.isEmpty()) {
            logger.info("Trainer " + username + " not found, nothing to update");
            return;
        }

        Trainer trainer = trainerOpt.get();

        logger.warning("this just writes true if trainer entity is managed -------> " + em.contains(trainer));
        logger.info("user found, updating trainer " + trainer.getUserName());
        trainer.setPassword(newPassword);
    }

    @Override
    public void updateTrainer(Long id,@NotNull Trainer trainer) {
        Optional<Trainer> oldTrainerOpt = findById(id);

        if (oldTrainerOpt.isEmpty()) {
            logger.info("Trainer " + trainer.getUserName() + " not found, nothing to update");
            return;
        }

        Trainer oldTrainer = oldTrainerOpt.get();

        logger.warning("this just writes true if trainer entity is managed -------> " + em.contains(oldTrainer));

        logger.info("user found, updating trainer " + trainer.getUserName());
        oldTrainer.setFirstName(trainer.getFirstName());
        oldTrainer.setLastName(trainer.getLastName());
        oldTrainer.setActive(trainer.isActive());
        oldTrainer.setSpecialization(trainer.getSpecialization());
        em.merge(oldTrainer);
        logger.warning("Username might be used or specialisation was invalid, please check");
    }

    @Override
    public void alterUserActivity(String username) {
        Optional<Trainer> trainerOpt = findByUsername(username);
        if (trainerOpt.isEmpty()) {
            logger.info("can not activate nonexistent trainer " + username);
            return;
        }

        Trainer trainer=trainerOpt.get();

        logger.info("user found, activating trainer " + trainer.getUserName());
        boolean active=trainer.isActive();
        trainer.setActive(!active);
        logger.info("activation completed");
    }

    @Override
    public List<Training> getTrainerTrainingsByUsername(String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainer.user.userName = :username",
                Training.class);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTrainerTrainingsByTraineeUsername(String traineeUsername, String trainerUsername) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainer.user.userName = :trainerUsername " +
                        "  and t.trainee.user.userName = :traineeUsername",
                Training.class);
        tq.setParameter("trainerUsername", trainerUsername);
        tq.setParameter("traineeUsername", traineeUsername);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTrainerTrainingsByFromDate(Date cutOffDate, String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate >= :cutOffDate" +
                        " and t.trainer.user.userName = :username",
                Training.class);
        tq.setParameter("cutOffDate", cutOffDate);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTrainerTrainingsByToDate(Date cutOffDate, String trainerUsername) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate <= :cutOffDate" +
                        " and t.trainer.user.userName = :trainerUsername",
                Training.class);
        tq.setParameter("cutOffDate", cutOffDate);
        tq.setParameter("trainerUsername", trainerUsername);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTrainerTrainingsBetweenDates(Date fromDate, Date toDate, String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate between :fromDate and :toDate" +
                        " and t.trainer.user.userName = :username",
                Training.class);
        tq.setParameter("fromDate", fromDate);
        tq.setParameter("toDate", toDate);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Training> getAllTrainings(String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                                                    "where t.trainer.user.userName = :username",
                                                     Training.class);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(em.find(Trainer.class, id));
    }

    @Override
    public List<Trainer> findAllTrainers() {
        TypedQuery<Trainer> tq = em.createQuery("select t from Trainer t ",Trainer.class);
        return tq.getResultList();
    }

}

