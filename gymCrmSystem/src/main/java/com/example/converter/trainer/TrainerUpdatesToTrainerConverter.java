package com.example.converter.trainer;

import com.example.dto.trainer.UpdatedTrainerDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class TrainerUpdatesToTrainerConverter implements Converter<UpdatedTrainerDTO, Trainer> {
    @Override
    public Trainer convert(@NotNull UpdatedTrainerDTO source) {
        Trainer trainer = new Trainer();
        trainer.setActive(source.getIsActive());
        trainer.setFirstName(source.getFirstName());
        trainer.setLastName(source.getLastName());
        return trainer;
    }
}
