package com.example.converter.trainer;

import com.example.dto.trainer.TrainerRegistrationDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TrainerRegistrationTrainerConverter implements Converter<TrainerRegistrationDTO, Trainer> {

    @Override
    public Trainer convert(@NotNull TrainerRegistrationDTO source) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(source.getFirstName());
        trainer.setLastName(source.getLastName());
        return trainer;
    }
}
