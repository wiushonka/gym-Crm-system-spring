package org.example.trainerworkloadservice.message;

import jakarta.jms.*;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ResponseDto;
import org.example.trainerworkloadservice.model.TrainerSummaryMongo;
import org.example.trainerworkloadservice.service.JwtService;
import org.example.trainerworkloadservice.service.TrainerSummaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class WorkloadConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadConsumer.class);

    private final TrainerSummaryService service;
    private final JwtService jwtService;

    @Autowired
    public WorkloadConsumer(TrainerSummaryService service, JwtService jwtService) {
        this.service = service;
        this.jwtService = jwtService;
    }

    @JmsListener(destination = "training.input.queue")
    public void consume(InputDto input) {
        log.info("Received message: {}", input);

        if(!jwtService.validate(input.getToken())){
            log.error("Invalid or outdated token");
            throw new RuntimeException("Missing JWT, move to DLQ");
        }

        service.processTraining(input);
    }

    @JmsListener(destination = "trainer.read.queue")
    public void receiveMessage(Message message, Session session) throws JMSException {
        if (!(message instanceof ObjectMessage objectMessage)) {
            throw new IllegalArgumentException("Message must be ObjectMessage");
        }

        InputDto input = (InputDto) objectMessage.getObject();

        if(!jwtService.validate(input.getToken())){
            log.error("Invalid or outdated token");
            throw new RuntimeException("Missing JWT, move to DLQ");
        }

        TrainerSummaryMongo resultSet = service.getTrainerSummary(input.getUsername());

        ResponseDto response = new ResponseDto.ResponseDtoBuilder().active(resultSet.getIsActive())
                .firstName(resultSet.getFirstName())
                .lastName(resultSet.getLastName())
                .username(input.getUsername())
                .yearlyMonthlyDuration(resultSet.getYearlyMonthlyDuration())
                .build();

        Destination replyDestination = message.getJMSReplyTo();
        if (replyDestination != null) {
            ObjectMessage replyMessage = session.createObjectMessage(response);
            MessageProducer producer = session.createProducer(replyDestination);
            producer.send(replyMessage);
            producer.close();
        }
    }
}
