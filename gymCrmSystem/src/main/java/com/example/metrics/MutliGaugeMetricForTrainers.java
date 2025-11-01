package com.example.metrics;

import com.example.service.TrainerService;
import com.example.storage.entitys.users.Trainer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@EnableScheduling
public class MutliGaugeMetricForTrainers {

    MultiGauge trainerTotalTrainings = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    protected TrainerService service;

    @Scheduled(fixedDelay = 10000)
    public void updateTraineeTrainingTimeGauges() {

        logger.info("10 second gone UPDATING TRAINER TRAININGS GAUGES :3");

        boolean overWrite = true;

        trainerTotalTrainings.register(
                service.getAllTrainers().stream().map(
                        (Trainer tr) -> MultiGauge.Row.of(Tags.of("id",""+tr.getId(),"username",tr.getUserName()),
                                                            service.getTrainerTrainingsByUsername(tr.getUserName()).size()))
                        .collect(Collectors.toList()),overWrite);
    }

    @Autowired
    public MutliGaugeMetricForTrainers(MeterRegistry registry){
        trainerTotalTrainings = MultiGauge.builder("trainer.total.trainings").tag("id","username").register(registry);
    }
}
