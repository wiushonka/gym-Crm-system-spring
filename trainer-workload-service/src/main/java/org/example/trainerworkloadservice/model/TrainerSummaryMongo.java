package org.example.trainerworkloadservice.model;



import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@DynamoDBTable(tableName = "TrainerSummary")
public class TrainerSummaryMongo {

    @DynamoDBHashKey(attributeName = "trainerUsername")
    private String trainerUsername;

    @DynamoDBAttribute(attributeName = "firstName")
    private String firstName;

    @DynamoDBAttribute(attributeName = "lastName")
    private String lastName;

    @DynamoDBAttribute(attributeName = "isActive")
    private Boolean isActive;

    @DynamoDBAttribute(attributeName = "yearlyMonthlyDuration")
    private Map<String, Map<String, Integer>> yearlyMonthlyDuration = new ConcurrentHashMap<>();

    public TrainerSummaryMongo() {}

    public TrainerSummaryMongo(String trainerUsername,
                               String firstName,
                               String lastName,
                               Boolean isActive,
                               Map<String, Map<String, Integer>> yearlyMonthlyDuration) {
        this.trainerUsername = trainerUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;

        this.yearlyMonthlyDuration = new ConcurrentHashMap<>();
        yearlyMonthlyDuration.forEach((year, monthMap) ->
                this.yearlyMonthlyDuration.put(year, new ConcurrentHashMap<>(monthMap))
        );
    }

    public String getTrainerUsername() { return trainerUsername; }

    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getIsActive() { return isActive; }

    public void setIsActive(Boolean active) { isActive = active; }

    public Map<String, Map<String, Integer>> getYearlyMonthlyDuration() { return yearlyMonthlyDuration; }

    public void setYearlyMonthlyDuration(Map<String, Map<String, Integer>> yearlyMonthlyDuration) {
        this.yearlyMonthlyDuration = new ConcurrentHashMap<>();
        yearlyMonthlyDuration.forEach((year, monthMap) ->
                this.yearlyMonthlyDuration.put(year, new ConcurrentHashMap<>(monthMap))
        );
    }
}
