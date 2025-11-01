package com.example.service;

import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public CustomUserDetailsService(TrainerService trainerService, TraineeService traineeService) {
        this.traineeService=traineeService;
        this.trainerService=trainerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Trainee> optTrainee = traineeService.getTraineeProfile(username);
        Optional<Trainer> optTrainer = trainerService.getTrainerProfile(username);
        if(optTrainee.isPresent()){
            Trainee trainee = optTrainee.get();
            return new User(trainee.getUserName(),
                            trainee.getPassword(),
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        }

        if(optTrainer.isPresent()){
            Trainer trainer = optTrainer.get();
            return new User(trainer.getUserName(),
                            trainer.getPassword(),
                            Collections.singleton(new SimpleGrantedAuthority("ROLE_TRAINER")));
        }

        throw new UsernameNotFoundException("User not found");
    }
}
