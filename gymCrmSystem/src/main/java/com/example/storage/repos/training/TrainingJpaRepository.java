package com.example.storage.repos.training;

import com.example.storage.entitys.training.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingJpaRepository extends JpaRepository<Training, Long> {
}
