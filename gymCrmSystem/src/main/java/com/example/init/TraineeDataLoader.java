package com.example.init;

import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.User;
import com.example.storage.repos.trainee.TraineeRepository;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class TraineeDataLoader implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(TraineeDataLoader.class);

    private final TraineeRepository repo;
    private final PasswordEncoder encoder;

    @Value("${trainee.data.file}")
    protected String initDataPath;

    @Autowired
    public TraineeDataLoader(TraineeRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // ASSUME FORMAT IS : birthDate,address,firstname,lastname,password,username

        try {
            logger.info("Loading trainee data from file: " + initDataPath);
            List<String> lines = Files.readAllLines(Paths.get(initDataPath));
            for (String line : lines) {
                String[] parts = line.split(",");
                Date date = df.parse(parts[0]);
                Trainee trainee = new Trainee(date,parts[1],new User(parts[2],parts[3], parts[5], encoder.encode(parts[4]),true));
                repo.save(trainee);
                logger.info("Loaded trainee: id=" + trainee.getId());
            }
            logger.info("Successfully loaded trainees");
        } catch (IOException e) {
            logger.warn("Failed to load trainee data from: " + initDataPath + " error: " + e.getMessage());
            throw new RuntimeException("Failed to load trainee data from " + initDataPath, e);
        } catch (ParseException e) {
            logger.warn("Failed to parse date while loading trainee data : " + e);
            throw new RuntimeException(e);
        }
    }
}
