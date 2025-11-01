package com.example.converter.trainee;

import com.example.dto.trainee.TrainerInTraineeDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TrainerToTrainerInTraineeDTOConverter implements Converter<Trainer, TrainerInTraineeDTO> {

    @Override
    public TrainerInTraineeDTO convert(@NotNull Trainer source) {
        return new TrainerInTraineeDTO(source.getUserName(),
                                        source.getFirstName(),
                                        source.getLastName(),
                                        source.getSpecialization().getTrainingTypeName());
    }
}


