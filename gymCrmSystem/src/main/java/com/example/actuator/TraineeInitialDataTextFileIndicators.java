package com.example.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


@Component
public class TraineeInitialDataTextFileIndicators implements HealthIndicator {

    @Value("${trainee.data.file}")
    private String traineeDataFile;

    @Value("${trainingTypes.data.file}")
    private String trainingTypesDataFile;

    @Override
    public Health health() {
        File traineeData = new File(traineeDataFile);

        try {
            Scanner traineeScanner = new Scanner(traineeData);
            if (traineeScanner.hasNextLine()) {
                return Health.up().withDetail("traineeData","is present and contains some data").build();
            }else {
                return Health.down().withDetail("traineeData","is present but does not contain any data").build();
            }
        } catch (FileNotFoundException e) {
            return Health.down().withDetail("traineeData","file can not found").build();
        }
    }
}
