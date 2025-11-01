package com.example.init;

import com.example.storage.entitys.training.TrainingType;
import com.example.storage.repos.trainingtype.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class TrainingTypeDataLoader implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(TrainingTypeDataLoader.class);

    @Value("${trainingTypes.data.file}")
    protected String initDataPath;

    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainingTypeDataLoader(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // ASSUME FORMAT IS : trainingTypeName

        try {
            logger.info("Loading TrainingTypes Data from file: " + initDataPath);
            List<String> lines = Files.readAllLines(Paths.get(initDataPath));
            for (String line : lines) {
                TrainingType trainingType = new TrainingType(line.strip());
                trainingTypeRepository.save(trainingType);
                logger.info("Loaded trainingType: id=" + trainingType.getId());
            }
            logger.info("Successfully loaded TrainingTypes");
        } catch (Exception e) {
            logger.warn("Failed to load TrainingTypes data from: " +
                    initDataPath + " error: " + e.getMessage());
            throw new RuntimeException("Failed to load trainee data from " + initDataPath, e);
        }
    }
}
