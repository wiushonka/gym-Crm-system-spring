package com.example.storage.repos.trainer;

import com.example.storage.entitys.users.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerJpaRepository extends JpaRepository<Trainer, Long> {
}
