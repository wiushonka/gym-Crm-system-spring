package com.example.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class InputDto implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private String token;
    private String transactionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Date trainingDate;

    private int trainingDuration;
    private ActionType actionType;

    public InputDto() {}

    public InputDto(InputDtoBuilder builder) {
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.isActive = builder.isActive;
        this.trainingDate = builder.trainingDate;
        this.trainingDuration = builder.trainingDuration;
        this.actionType = builder.actionType;
    }

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

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getTransactionId() { return transactionId; }

    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    @Override
    public String toString() {
        return "InputDto{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isActive=" + isActive +
                ", token='" + token + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", trainingDate=" + trainingDate +
                ", trainingDuration=" + trainingDuration +
                ", actionType=" + actionType +
                '}';
    }

    public static class InputDtoBuilder {
        private String username;
        private String firstName;
        private String lastName;
        private boolean isActive;
        private Date trainingDate;
        private ActionType actionType;
        private int trainingDuration;

        public InputDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public InputDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public InputDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public InputDtoBuilder active(boolean active) {
            this.isActive = active;
            return this;
        }

        public InputDtoBuilder trainingDate(Date trainingDate) {
            this.trainingDate = trainingDate;
            return this;
        }

        public InputDtoBuilder actionType(ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public InputDtoBuilder trainingDuration(int trainingDuration) {
            this.trainingDuration = trainingDuration;
            return this;
        }

        public InputDto build() {
            return new InputDto(this);
        }
    }
}
