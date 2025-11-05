package org.example.trainerworkloadservice.dto;

import java.io.Serializable;
import java.util.Map;

public class ResponseDto implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private Map<String, Map<String, Integer>> yearlyMonthlyDuration;

    public ResponseDto() {}

    public ResponseDto(String username, String firstName, String lastName, boolean isActive, Map<String, Map<String, Integer>> yearlyMonthlyDuration) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.yearlyMonthlyDuration = yearlyMonthlyDuration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Map<String, Map<String, Integer>> getYearlyMonthlyDuration() {
        return yearlyMonthlyDuration;
    }

    public void setYearlyMonthlyDuration(Map<String, Map<String, Integer>> yearlyMonthlyDuration) {
        this.yearlyMonthlyDuration = yearlyMonthlyDuration;
    }

    public static class ResponseDtoBuilder {
        private String username;
        private String firstName;
        private String lastName;
        private boolean isActive;
        private Map<String, Map<String, Integer>> yearlyMonthlyDuration;

        public ResponseDtoBuilder() {}

        public ResponseDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ResponseDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ResponseDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ResponseDtoBuilder active(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public ResponseDtoBuilder yearlyMonthlyDuration(Map<String, Map<String, Integer>> yearlyMonthlyDuration) {
            this.yearlyMonthlyDuration = yearlyMonthlyDuration;
            return this;
        }

        public ResponseDto build() {
            return new ResponseDto(username, firstName, lastName, isActive, yearlyMonthlyDuration);
        }
    }
}
