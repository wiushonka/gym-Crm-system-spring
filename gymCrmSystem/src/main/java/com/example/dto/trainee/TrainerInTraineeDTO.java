package com.example.dto.trainee;

public class TrainerInTraineeDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;

    public TrainerInTraineeDTO(String username, String firstName, String lastName, String specialization) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    public String getUsername() { return username; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getSpecialization() { return specialization; }

    public void setUsername(String username) { this.username = username; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
