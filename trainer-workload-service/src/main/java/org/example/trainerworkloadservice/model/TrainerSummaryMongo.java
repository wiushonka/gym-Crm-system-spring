package org.example.trainerworkloadservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection = "trainer_summary")
public class TrainerSummaryMongo {

    @Id
    private String id;

    @Indexed(unique = true)
    private String trainerUsername;

    private String firstName;
    private String lastName;
    private Boolean isActive;

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

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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
