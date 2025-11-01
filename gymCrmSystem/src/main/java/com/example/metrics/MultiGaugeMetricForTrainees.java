package com.example.metrics;

import com.example.service.TraineeService;
import com.example.storage.entitys.users.Trainee;
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
public class MultiGaugeMetricForTrainees {

    MultiGauge traineesActivity = null;

    private final Logger logger = LoggerFactory.getLogger(MultiGaugeMetricForTrainees.class);

    @Lazy
    @Autowired
    protected TraineeService service;

    @Scheduled(fixedRate = 10000)
    public void updateTraineesActivityGauges() {

        logger.info("10 second gone UPDATING TRAINEE ACTIVITY GAUGES :3");

        boolean overWrite = true;

        traineesActivity.register(
                service.getAllTrainees().stream().
                    map(
                        (Trainee tr) -> MultiGauge.Row.of(Tags.of("trid",""+tr.getId(),"trusername",tr.getUserName()),tr.isActive()?1:0)
                        ).collect(Collectors.toList()),overWrite);
    }

    @Autowired
    public MultiGaugeMetricForTrainees(MeterRegistry registry){
        traineesActivity = MultiGauge.builder("trainees.activities").tag("trid","trusername").register(registry);
    }
}
