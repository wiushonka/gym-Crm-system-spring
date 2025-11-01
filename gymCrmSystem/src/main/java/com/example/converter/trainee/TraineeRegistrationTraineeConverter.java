package com.example.converter.trainee;

import com.example.dto.trainee.TraineeRegistrationDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainee;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TraineeRegistrationTraineeConverter implements Converter<TraineeRegistrationDTO,Trainee> {
    @Override
    public Trainee convert(@NotNull TraineeRegistrationDTO source) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(source.getFirstName());
        trainee.setLastName(source.getLastName());
        if(source.getAddress().isPresent()) trainee.setAddress(source.getAddress().get());
        if(source.getBirthDate().isPresent()) trainee.setDateOfBirth(source.getBirthDate().get());
        return trainee;
    }
}
