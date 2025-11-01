package com.example.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class TrainerInitialDataTextFileIndicator implements HealthIndicator {

    @Value("${trainer.data.file}")
    private String trainerDataFile;

    @Override
    public Health health() {
        File file = new File(trainerDataFile);

        try{
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextLine()){
                return Health.up().withDetail("trainerData","file is present and is filled with some data").build();
            }else{
                return Health.down().withDetail("trainerData","file is empty").build();
            }
        } catch (FileNotFoundException e) {
            return Health.down().withDetail("trainerData","file can not found").build();
        }
    }
}
