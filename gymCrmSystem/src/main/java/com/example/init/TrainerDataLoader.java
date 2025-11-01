package com.example.init;

import com.example.storage.entitys.training.TrainingType;
import com.example.storage.entitys.users.Trainer;
import com.example.storage.entitys.users.User;
import com.example.storage.repos.trainer.TrainerRepository;
import com.example.storage.repos.trainingtype.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
public class TrainerDataLoader implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(TrainerDataLoader.class);

    @Value("${trainer.data.file}")
    protected String initDataPath;

    private final PasswordEncoder encoder;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainerDataLoader(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository,
                             PasswordEncoder encoder) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing TrainerStorage from file " + initDataPath);

        // ASSUME FORMAT IS : firstName,lastName,password,username,spec

        try {
            List<String> lines = Files.readAllLines(Paths.get(initDataPath));
            for (String line : lines) {
                String[] parts = line.split(",");
                TrainingType type;
                Optional<TrainingType> typeOpt = trainingTypeRepository.findByName(parts[4]);
                if(typeOpt.isEmpty()) {
                    type = new TrainingType(parts[4]);
                    trainingTypeRepository.save(type);
                }else{
                    type = typeOpt.get();
                }
                Trainer trainer = new Trainer(new User(parts[0],parts[1],parts[3],encoder.encode(parts[2]),true),type);
                trainerRepository.save(trainer);
                logger.info("Loaded trainer: id=" + trainer.getId());
            }
            logger.info("Successfully loaded trainers");
        } catch (IOException e) {
            logger.warn("Failed to load trainer data from " + initDataPath);
            throw new RuntimeException("Failed to load trainer data from " + initDataPath, e);
        }
    }
}
