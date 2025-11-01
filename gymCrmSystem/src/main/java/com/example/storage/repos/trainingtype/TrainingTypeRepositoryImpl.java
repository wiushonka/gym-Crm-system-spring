package com.example.storage.repos.trainingtype;
import com.example.storage.entitys.training.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
@Transactional
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    private static final Logger logger = Logger.getLogger(TrainingTypeRepositoryImpl.class.getName());

    private final EntityManager em;

    @Autowired
    public TrainingTypeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(TrainingType trainingType) {
        Optional<TrainingType> opt = findByName(trainingType.getTrainingTypeName());
        if (opt.isPresent()) {
            logger.warning("TrainingType " + trainingType.getTrainingTypeName() + " already exists");
            return;
        }
        logger.info("storing trainingType : " + trainingType.getTrainingTypeName());
        em.persist(trainingType);
        logger.info("trainingType stored successfully");
    }

    @Override
    public Optional<TrainingType> findByName(String trainingTypeName) {
        TypedQuery<TrainingType> tq = em.createQuery("select tp from TrainingType tp " +
                                                        "where tp.trainingTypeName = :name",
                                                        TrainingType.class);
        tq.setParameter("name", trainingTypeName);
        List<TrainingType> results = tq.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<TrainingType> findAllTrainingTypes() {
        TypedQuery<TrainingType> tq = em.createQuery("select tp from TrainingType tp", TrainingType.class);
        return tq.getResultList();
    }
}
