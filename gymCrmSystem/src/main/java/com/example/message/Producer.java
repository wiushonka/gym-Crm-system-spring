package com.example.message;

import com.example.service.JwtService;
import jakarta.jms.*;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ResponseDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

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
        jmsTemplate.convertAndSend("training.input.queue",input);
        log.info("Sent message: {} , into training.input.queue", input);
    }

    public ResponseDto produceReadRequest(InputDto input) {
        addTokenAndTransactionId(input);
        return jmsTemplate.execute(session -> {
            TemporaryQueue replyQueue = session.createTemporaryQueue();

            ObjectMessage message = session.createObjectMessage(input);
            message.setJMSReplyTo(replyQueue);

            MessageProducer producer = session.createProducer(session.createQueue("trainer.read.queue"));
            producer.send(message);

            MessageConsumer consumer = session.createConsumer(replyQueue);
            Message responseMessage = consumer.receive(5000);
            if (responseMessage == null) {
                throw new RuntimeException("Timeout waiting for response");
            }

            if (responseMessage instanceof ObjectMessage) {
                return (ResponseDto) ((ObjectMessage) responseMessage).getObject();
            } else {
                throw new RuntimeException("Invalid response type");
            }
        }, true);
    }
}
