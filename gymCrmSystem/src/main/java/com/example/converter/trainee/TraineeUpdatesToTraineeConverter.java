package com.example.converter.trainee;

import com.example.dto.trainee.UpdatedTraineeDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainee;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TraineeUpdatesToTraineeConverter implements Converter<UpdatedTraineeDTO,Trainee> {

    @Override
    public Trainee convert(@NotNull UpdatedTraineeDTO source) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(source.getFirstName());
        trainee.setLastName(source.getLastName());
        trainee.setActive(source.isActive());
        if(source.getDateOfBirth().isPresent()) trainee.setDateOfBirth(source.getDateOfBirth().get());
        if(source.getAddress().isPresent()) trainee.setAddress(source.getAddress().get());
        return trainee;
    }
}
