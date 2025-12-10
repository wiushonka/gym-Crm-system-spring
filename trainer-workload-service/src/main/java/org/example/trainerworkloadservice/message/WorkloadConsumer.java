package org.example.trainerworkloadservice.message;

import jakarta.jms.*;
import org.example.trainerworkloadservice.dto.ActionType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public void consume(Map<String, Object> map) {
        InputDto input = mapToInputDto(map);

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

        Object obj = objectMessage.getObject();
        if (!(obj instanceof Map<?, ?> mapObj)) {
            throw new IllegalArgumentException("Expected ObjectMessage containing Map<String,Object>");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> requestMap = (Map<String, Object>) mapObj;

        log.info("Received read request: {}", requestMap);

        String token = (String) requestMap.get("token");
        if (!jwtService.validate(token)) {
            log.error("Invalid or expired token");
            throw new RuntimeException("JWT validation failed");
        }

        String username = (String) requestMap.get("username");
        TrainerSummaryMongo resultSet = service.getTrainerSummary(username);

        ResponseDto responseDto = new ResponseDto.ResponseDtoBuilder()
                .username(username)
                .firstName(resultSet.getFirstName())
                .lastName(resultSet.getLastName())
                .active(resultSet.getIsActive())
                .yearlyMonthlyDuration(resultSet.getYearlyMonthlyDuration())
                .build();

        Map<String, Object> responseMap = responseDtoToMap(responseDto);

        Destination replyDestination = message.getJMSReplyTo();
        if (replyDestination != null) {
            ObjectMessage replyMessage = session.createObjectMessage((Serializable) responseMap);
            MessageProducer producer = session.createProducer(replyDestination);
            producer.send(replyMessage);
            producer.close();
        }
    }

    private Map<String, Object> responseDtoToMap(ResponseDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", dto.getUsername());
        map.put("firstName", dto.getFirstName());
        map.put("lastName", dto.getLastName());
        map.put("isActive", dto.isActive());
        map.put("yearlyMonthlyDuration", dto.getYearlyMonthlyDuration());
        return map;
    }

    private InputDto mapToInputDto(Map<String, Object> map) {
        InputDto input = new InputDto();
        input.setUsername((String) map.get("username"));
        input.setFirstName((String) map.get("firstName"));
        input.setLastName((String) map.get("lastName"));
        input.setActive(Boolean.TRUE.equals(map.get("isActive")));
        input.setToken((String) map.get("token"));
        input.setTransactionId((String) map.get("transactionId"));

        Object trainingDateObj = map.get("trainingDate");
        if (trainingDateObj instanceof Date) {
            input.setTrainingDate((Date) trainingDateObj);
        } else if (trainingDateObj instanceof Long) {
            input.setTrainingDate(new Date((Long) trainingDateObj));
        } else {
            input.setTrainingDate(null);
        }

        Object durationObj = map.get("trainingDuration");
        if (durationObj instanceof Number) {
            input.setTrainingDuration(((Number) durationObj).intValue());
        }

        Object actionObj = map.get("actionType");
        if (actionObj instanceof String) {
            input.setActionType(ActionType.valueOf((String) actionObj));
        }

        return input;
    }
}
