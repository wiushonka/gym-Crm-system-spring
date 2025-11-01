package com.example.storage.repos.trainee;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.logging.Logger;

@Repository
@Transactional
public class TraineeRepositoryImpl implements TraineeRepository {

    private static final Logger logger = Logger.getLogger(TraineeRepositoryImpl.class.getName());

    private final EntityManager em;

    @Autowired
    public TraineeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Trainee trainee) {
        logger.info("saving trainee " + trainee);
        em.persist(trainee);
        logger.info("saved trainee " + trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(em.find(Trainee.class, id));
    }

    @Override
    public Boolean isValidTrainee(String username, String password) {
        Optional<Trainee> trainee = findByUsername(username);
        if (trainee.isEmpty()) return false;
        return trainee.get().getPassword().equals(password);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        TypedQuery<Trainee> tq = em.createQuery("select t from Trainee t " +
                        "where t.user.userName=:username",
                Trainee.class);
        tq.setParameter("username", username);
        List<Trainee> list = tq.setMaxResults(1).getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if (traineeOpt.isEmpty()) {
            logger.info("Trainee " + username + " not found, nothing to update");
            return;
        }

        Trainee trainee = traineeOpt.get();
        logger.warning("this just writes true if trainee entity is managed -------> " + em.contains(trainee));
        logger.info("user found, updating trainee " + trainee.getUserName());
        trainee.setPassword(newPassword);
    }

    @Override
    public void updateTrainee(Long id, @NotNull Trainee trainee) {
        Optional<Trainee> oldTraineeOpt = findById(id);

        if (oldTraineeOpt.isEmpty()) {
            logger.info("Trainee id: " + id + " not found, nothing to update");
            return;
        }

        Trainee oldTrainee = oldTraineeOpt.get();

        logger.warning("this just writes true if trainee entity is managed -------> " + em.contains(oldTrainee));

        logger.info("user found, updating trainee id: " + id);

        oldTrainee.setFirstName(trainee.getFirstName());
        oldTrainee.setLastName(trainee.getLastName());
        oldTrainee.setActive(trainee.isActive());

        if(trainee.getDateOfBirth() != null) oldTrainee.setDateOfBirth(trainee.getDateOfBirth());
        if(trainee.getAddress() != null) oldTrainee.setAddress(trainee.getAddress());

        em.merge(oldTrainee);
        logger.warning("Username might be user, please check");
    }

    @Override
    public void alterUserActivity(String username) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if (traineeOpt.isEmpty()) {
            logger.info("can not activate nonexistent trainee " + username);
            return;
        }

        Trainee trainee=traineeOpt.get();

        logger.info("user found, activating trainee " + trainee.getUserName());
        boolean active=trainee.isActive();
        trainee.setActive(!active);
        logger.info("activation completed");
    }

    @Override
    public List<Training> getTraineeTrainingsByTraineeUsername(String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainee.user.userName = :username",
                Training.class);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTraineeTrainingsByTrainerUsername(String traineeUsername, String trainerUsername) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainer.user.userName = :trainerUsername " +
                        "  and t.trainee.user.userName = :traineeUsername",
                Training.class);
        tq.setParameter("trainerUsername", trainerUsername);
        tq.setParameter("traineeUsername", traineeUsername);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTraineeTrainingsByFromDate(Date cutOffDate, String traineeUsername) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate >= :cutOffDate " +
                        " and t.trainee.user.userName = :traineeUsername",
                Training.class);
        tq.setParameter("cutOffDate", cutOffDate);
        tq.setParameter("traineeUsername", traineeUsername);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTraineeTrainingsByToDate(Date cutOffDate, String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate <= :cutOffDate " +
                        " and t.trainee.user.userName = :username",
                Training.class);
        tq.setParameter("cutOffDate", cutOffDate);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Training> getTraineeTrainingsBetweenDates(Date fromDate, Date toDate, String username) {
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                        "where t.trainingDate between :fromDate and :toDate" +
                        " and t.trainee.user.userName = :username",
                Training.class);
        tq.setParameter("fromDate", fromDate);
        tq.setParameter("toDate", toDate);
        tq.setParameter("username", username);
        return tq.getResultList();
    }

    @Override
    public List<Trainer> getTraineeUnassignedTrainers(Trainee trainee) {
        TypedQuery<Trainer> tq=em.createQuery("select t from Trainer t " +
                        "where :trainee not member of t.trainees",
                Trainer.class);
        tq.setParameter("trainee", trainee);
        return tq.getResultList();
    }

    @Override
    public void addTrainerToTrainee(Trainer trainer, String username) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if(traineeOpt.isEmpty()){
            logger.severe("can not add trainer to non existing trainee " + trainer.getUserName());
            return;
        }

        if(trainer.getId()==null){
            logger.severe("can not add non existing trainer to trainee");
            return;
        }

        Trainee trainee = traineeOpt.get();
        Trainer managedTrainer = em.find(Trainer.class, trainer.getId());
        if (managedTrainer == null) {
            logger.severe("trainer not found id=" + trainer.getId());
            return;
        }

        managedTrainer.addTrainee(trainee);
        logger.info("added successfully");
    }

    @Override
    public void removeTrainerFromTrainee(Trainer trainer, String username) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if(traineeOpt.isEmpty()){
            logger.severe("can not add trainer to non existing trainee " + trainer.getUserName());
            return;
        }

        if(trainer.getId()==null){
            logger.severe("can not add non existing trainer to trainee");
            return;
        }

        Trainee trainee = traineeOpt.get();
        Trainer managedTrainer = em.find(Trainer.class, trainer.getId());
        if (managedTrainer == null) {
            logger.severe("trainer not found id=" + trainer.getId());
            return;
        }

        trainee.removeTrainer(managedTrainer);
        managedTrainer.removeTrainee(trainee);

        Iterator<Training> it = trainee.getTrainings().iterator();
        while (it.hasNext()) {
            Training training = it.next();
            if (training.getTrainer().getId().equals(managedTrainer.getId())) {
                managedTrainer.removeTraining(training);
                it.remove();
                Training managedTraining = em.merge(training);
                em.remove(managedTraining);
            }
        }

        logger.info("removed successfully");
    }

    @Override
    public void deleteTrainee(@NotNull Trainee trainee){
        deleteTrainee(trainee.getUserName());
    }

    @Override
    public void deleteTrainee(String username) {
        Optional<Trainee> traineeOpt = findByUsername(username);
        if (traineeOpt.isEmpty()) {
            logger.severe("can not remove non existing trainee");
            return;
        }

        Trainee trainee = em.merge(traineeOpt.get());
        for (Trainer tr : trainee.getTrainers()) {
            tr.removeTrainee(trainee);
        }
        trainee.setTrainers(new ArrayList<>());

        for (Training training : trainee.getTrainings()) {
            Trainer tr = training.getTrainer();
            tr.removeTraining(training);
            Training managedTraining = em.merge(training);
            em.remove(managedTraining);
        }
        trainee.setTrainers(new ArrayList<>());

        em.remove(trainee);
        logger.info("removed successfully");
    }

    @Override
    public List<Trainee> findAllTrainees() {
        TypedQuery<Trainee> tq = em.createQuery("select t from Trainee t ",Trainee.class);
        return tq.getResultList();
    }
}
