package com.example.message;

import com.example.dto.message.InputDto;
import com.example.service.JwtService;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class Producer {

    private final SqsTemplate sqsTemplate;
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    public Producer(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void produce(@NotNull InputDto input) {

        Map<String, Object> map = new HashMap<>();
        map.put("username", input.getUsername());
        map.put("firstName", input.getFirstName());
        map.put("lastName", input.getLastName());
        map.put("isActive", input.isActive());
        map.put("token", input.getToken());
        map.put("transactionId", input.getTransactionId());
        map.put("trainingDate", input.getTrainingDate());
        map.put("trainingDuration", input.getTrainingDuration());
        map.put("actionType", input.getActionType());

        sqsTemplate.send("gym-Queue.fifo", map);
        log.info("-----------------------------------------------------");
        log.info("Sent message: {} , into training.input.queue", map);
        log.info("-----------------------------------------------------");
    }
}
