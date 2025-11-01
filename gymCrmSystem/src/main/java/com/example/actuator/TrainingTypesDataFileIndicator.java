package com.example.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class TrainingTypesDataFileIndicator implements HealthIndicator {

    @Value("${trainingTypes.data.file}")
    private String trainingTypesDataFile;

    @Override
    public Health health() {
        File trainingTypesData = new File(trainingTypesDataFile);

        try {
            Scanner scanner = new Scanner(trainingTypesData);
            if (scanner.hasNextLine()) {
                return Health.up().withDetail("trainingTypesData","is present and contains some data").build();
            }else {
                return Health.down().withDetail("trainingTypesData","is present but does not contain any data").build();
            }
        } catch (FileNotFoundException e) {
            return Health.down().withDetail("trainingTypesData","file can not found").build();
        }
    }
}

