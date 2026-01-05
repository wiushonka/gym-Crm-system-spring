package org.example.trainerworkloadservice.message;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.example.trainerworkloadservice.dto.ActionType;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.service.TrainerSummaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class WorkloadConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadConsumer.class);

    private final TrainerSummaryService service;

    @Autowired
    public WorkloadConsumer(TrainerSummaryService service) {
        this.service = service;
    }
    
    @SqsListener("gym-Queue.fifo")
    public void consume(Map<String, Object> map) {
        InputDto input = mapToInputDto(map);

        log.info("--------------------------------------------------------------------------------------");
        log.info("Received message: {}", input);
        log.info("--------------------------------------------------------------------------------------");

        service.processTraining(input);
    }

    private InputDto mapToInputDto(Map<String, Object> map) {
        InputDto input = new InputDto();

        input.setUsername(getString(map.get("username")));
        input.setFirstName(getString(map.get("firstName")));
        input.setLastName(getString(map.get("lastName")));
        input.setToken(getString(map.get("token")));
        input.setTransactionId(getString(map.get("transactionId")));

        input.setActive(getBoolean(map.get("isActive")));


        input.setTrainingDuration(getInteger(map.get("trainingDuration")));

        input.setActionType(getActionType(map.get("actionType")));

        input.setTrainingDate(getDate(map.get("trainingDate")));

        return input;
    }


    private String getString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }

    private boolean getBoolean(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof String) {
            String str = ((String) obj).toLowerCase();
            return str.equals("true") || str.equals("1") || str.equals("yes") || str.equals("y");
        }
        if (obj instanceof Number) return ((Number) obj).intValue() != 0;
        return false;
    }

    private Date getDate(Object dateObj) {
        if (dateObj == null) return null;

        try {
            if (dateObj instanceof Date) {
                return (Date) dateObj;
            }

            String dateStr = dateObj.toString();

            Instant instant = Instant.parse(dateStr);
            return Date.from(instant);

        } catch (Exception e) {
            log.warn("Failed to parse date: {} - {}", dateObj, e.getMessage());
            return null;
        }
    }

    private Integer getInteger(Object numObj) {
        if (numObj == null) return 0;

        try {
            if (numObj instanceof Number) {
                return ((Number) numObj).intValue();
            }
            if (numObj instanceof String) {
                return Integer.parseInt((String) numObj);
            }
        } catch (Exception e) {
            log.warn("Failed to parse integer: {} - {}", numObj, e.getMessage());
        }

        return 0;
    }

    private ActionType getActionType(Object actionObj) {
        if (actionObj == null) return null;

        try {
            if (actionObj instanceof ActionType) {
                return (ActionType) actionObj;
            }
            if (actionObj instanceof String) {
                String actionStr = (String) actionObj;
                for (ActionType type : ActionType.values()) {
                    if (type.name().equalsIgnoreCase(actionStr)) {
                        return type;
                    }
                }
                return ActionType.valueOf(actionStr);
            }
        } catch (Exception e) {
            log.warn("Failed to parse ActionType: {} - {}", actionObj, e.getMessage());
        }

        return null;
    }
}
