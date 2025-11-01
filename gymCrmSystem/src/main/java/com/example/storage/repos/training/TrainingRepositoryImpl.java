package com.example.storage.repos.training;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
@Transactional
public class TrainingRepositoryImpl implements TrainingRepository {

    private static final Logger logger = Logger.getLogger(TrainingRepositoryImpl.class.getName());

    private final EntityManager em;

    @Autowired
    public TrainingRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Training training) {
        logger.info("Adding training: " + training.getTrainingName());
        em.persist(training);
        training.getTrainee().addTraining(training);
        training.getTrainer().addTraining(training);
        logger.info("Training added: " + training.getTrainingName());
    }

    @Override
    public List<Training> findAllTrainings() {
        TypedQuery<Training> tq = em.createQuery("select t from Training t ",Training.class);
        return tq.getResultList();
    }

    @Override
    public Optional<Training> findTrainingById(Long id){
        return Optional.ofNullable(em.find(Training.class, id));
    }

    @Override
    public Optional<Training> findTrainingByName(String name){
        TypedQuery<Training> tq = em.createQuery("select t from Training t " +
                                                    "where t.trainingName=:name",Training.class);
        tq.setParameter("name", name);
        try{
            Training training = tq.getSingleResult();
            return Optional.ofNullable(training);
        }catch (Exception e){
            logger.warning("Could not find training -> " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void delete(Training training) {
        if(!em.contains(training)){
            logger.info("merging training: " + training.getTrainingName());
            em.merge(training);
        }

        Trainer trainer = training.getTrainer();
        trainer.removeTraining(training);
        Trainee trainee = training.getTrainee();
        trainee.removeTraining(training);

        em.remove(training);

        logger.info("Training deleted: " + training.getTrainingName());
    }
}
