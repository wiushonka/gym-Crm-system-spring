package com.example.converter.trainer;


import com.example.dto.trainer.TraineesInTrainerDTO;
import com.example.dto.trainer.TrainerDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrainerToTrainerDTOConverter implements Converter< Trainer,TrainerDTO> {
    @Override
    public TrainerDTO convert(@NotNull Trainer source) {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstname(source.getFirstName());
        trainerDTO.setLastname(source.getLastName());
        trainerDTO.setIsActive(source.isActive());
        trainerDTO.setSpecializationName(source.getSpecializationName());
        List<TraineesInTrainerDTO> lst = new ArrayList<>();
        for(Trainee t :source.getTrainees()){
            TraineesInTrainerDTO dto = new TraineesInTrainerDTO(t.getFirstName(),t.getLastName(),t.getUserName());
            lst.add(dto);
        }
        trainerDTO.setTraineesList(lst);
        return trainerDTO;
    }
}
