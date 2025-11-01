package com.example.converter.trainer;

import com.example.dto.user.LoginDTO;
import com.example.converter.Converter;
import com.example.storage.entitys.users.Trainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class LoginToTrainerConverter implements Converter<LoginDTO, Trainer> {

    @Override
    public Trainer convert(@NotNull LoginDTO source) {
        Trainer trainer = new Trainer();
        trainer.setUserName(source.getUserName());
        trainer.setPassword(source.getPassword());
        return trainer;
    }
}
