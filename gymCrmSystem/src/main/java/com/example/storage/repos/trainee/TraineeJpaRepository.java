package com.example.storage.repos.trainee;

import com.example.storage.entitys.users.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TraineeJpaRepository extends JpaRepository<Trainee, Long> {
}
