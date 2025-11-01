package org.example.trainerworkloadservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class InputDto {

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Date trainingDate;

    private int trainingDuration;
    private ActionType actionType;

    public InputDto() {}

    public InputDto(String username, String firstName, String lastName, boolean isActive,
                    Date trainingDate, int trainingDuration, ActionType actionType) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = actionType;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { isActive = active; }

    public Date getTrainingDate() { return trainingDate; }

    public void setTrainingDate(Date trainingDate) { this.trainingDate = trainingDate; }

    public int getTrainingDuration() { return trainingDuration; }

    public void setTrainingDuration(int trainingDuration) { this.trainingDuration = trainingDuration; }

    public ActionType getActionType() { return actionType; }

    public void setActionType(ActionType actionType) { this.actionType = actionType; }
}
