package com.example.metrics;

import com.example.service.TrainingService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TrainingCustomMetric {
    @Lazy
    @Autowired
    protected TrainingService service;

    public Supplier<Number> fetchTrainingsCount() {
        return ()-> service.getTrainings().size();
    }

    @Autowired
    public TrainingCustomMetric(MeterRegistry registry) {
        Gauge.builder("total_number_of_trainings", fetchTrainingsCount())
                .tag("version","v1")
                .description("this shows total number of trainings avalable")
                .register(registry);
    }
}
