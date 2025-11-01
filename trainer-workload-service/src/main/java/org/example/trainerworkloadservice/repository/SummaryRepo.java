package org.example.trainerworkloadservice.repository;

import org.example.trainerworkloadservice.model.TrainerSummaryMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryRepo extends MongoRepository<TrainerSummaryMongo, String> {
    Optional<TrainerSummaryMongo> findByTrainerUsername(String username);
}
