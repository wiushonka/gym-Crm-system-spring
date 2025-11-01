package com.example.converter.trainee;

import com.example.dto.trainee.TraineeDTO;
import com.example.dto.trainee.TrainerInTraineeDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TraineeToTraineeDTOConverter implements Converter<Trainee, TraineeDTO> {
    @Override
    public TraineeDTO convert(Trainee source) {
        List<TrainerInTraineeDTO> lst = new ArrayList<>();
        for(Trainer t : source.getTrainers()) {
            lst.add(new TrainerInTraineeDTO(t.getUserName(),
                                            t.getFirstName(),
                                            t.getLastName(),
                                            t.getSpecialization().getTrainingTypeName()));
        }
        return new TraineeDTO(source.getFirstName(),
                                        source.getLastName(),
                                        source.getDateOfBirth(),
                                        source.getAddress(),
                                        source.isActive(),lst);
    }
}
