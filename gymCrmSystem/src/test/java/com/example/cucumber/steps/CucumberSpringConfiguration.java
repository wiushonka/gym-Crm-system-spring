package com.example.cucumber.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
import org.example.trainerworkloadservice.TrainerWorkloadServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    private static final Logger log = LoggerFactory.getLogger(CucumberSpringConfiguration.class);
    private static ConfigurableApplicationContext workloadMicroservice;

    @PostConstruct
    public void startWorkloadMicroservice() {
        log.info("starting workload microservice");
        if(workloadMicroservice==null) {
            workloadMicroservice = new SpringApplicationBuilder(TrainerWorkloadServiceApplication.class)
                    .profiles("test").run();

            log.info("workload microservice started, active profile is set as => test !");
        }
        log.warn("MAKE SURE ARTEMIS IS RUNNING !!!!!");
        log.info("workload microservice test profile loaded successfully! :3");
    }
}