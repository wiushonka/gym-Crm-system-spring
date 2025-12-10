package com.example.message;

import com.example.dto.message.InputDto;
import com.example.dto.message.ResponseDto;
import com.example.service.JwtService;
import jakarta.jms.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class Producer {

    private final JmsTemplate jmsTemplate;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    public Producer(JmsTemplate jmsTemplate, JwtService jwtService) {
        this.jmsTemplate = jmsTemplate;
        this.jwtService = jwtService;
    }

    private void addTokenAndTransactionId(InputDto input) {
        String token=jwtService.generateJwtToken("monolith-service");
        input.setToken(token);

        String transactionId= MDC.get("transactionId");
        if(transactionId==null){
            transactionId= UUID.randomUUID().toString();
            log.info("Transaction Id was null, now is {}", transactionId);
        }
        input.setTransactionId(transactionId);
    }

    public void produce(@NotNull InputDto input) {
        addTokenAndTransactionId(input);

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

        jmsTemplate.convertAndSend("training.input.queue",map);
        log.info("Sent message: {} , into training.input.queue", input);
    }

    public ResponseDto produceReadRequest(InputDto input) {
        addTokenAndTransactionId(input);

        return jmsTemplate.execute(session -> {
            TemporaryQueue replyQueue = session.createTemporaryQueue();

            Map<String, Object> map = new HashMap<>();
            map.put("username", input.getUsername());
            map.put("firstName", input.getFirstName());
            map.put("lastName", input.getLastName());
            map.put("isActive", input.isActive());
            map.put("token", input.getToken());
            map.put("transactionId", input.getTransactionId());
            map.put("trainingDate", input.getTrainingDate());
            map.put("trainingDuration", input.getTrainingDuration());
            map.put("actionType", input.getActionType().name());

            ObjectMessage message = session.createObjectMessage((Serializable) map);
            message.setJMSReplyTo(replyQueue);

            MessageProducer producer = session.createProducer(session.createQueue("trainer.read.queue"));
            producer.send(message);

            MessageConsumer consumer = session.createConsumer(replyQueue);
            Message responseMessage = consumer.receive(5000);

            if (responseMessage == null) {
                throw new RuntimeException("Timeout waiting for response");
            }

            if (responseMessage instanceof ObjectMessage objectResponse) {
                Object obj = objectResponse.getObject();
                if (obj instanceof Map<?, ?> responseMap) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> respMap = (Map<String, Object>) responseMap;
                    return mapToResponseDto(respMap);
                } else {
                    throw new RuntimeException("Invalid response type");
                }
            } else {
                throw new RuntimeException("Invalid JMS response type");
            }
        }, true);
    }

    private ResponseDto mapToResponseDto(Map<String, Object> map) {
        ResponseDto.ResponseDtoBuilder builder = new ResponseDto.ResponseDtoBuilder();
        builder.username((String) map.get("username"));
        builder.firstName((String) map.get("firstName"));
        builder.lastName((String) map.get("lastName"));
        builder.active(Boolean.TRUE.equals(map.get("isActive")));
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Integer>> durations =
                (Map<String, Map<String, Integer>>) map.get("yearlyMonthlyDuration");
        builder.yearlyMonthlyDuration(durations);
        return builder.build();
    }
}
