package com.example.dto.trainee;

import java.util.Date;
import java.util.List;

public class TraineeDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<TrainerInTraineeDTO> trainersList;

    public TraineeDTO(String firstName, String lastName, Date dateOfBirth,
                      String address, Boolean isActive, List<TrainerInTraineeDTO> trainersList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
        this.trainersList = trainersList;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public Date getDateOfBirth() { return dateOfBirth; }

    public String getAddress() { return address; }

    public Boolean getIsActive() { return isActive; }

    public List<TrainerInTraineeDTO> getTrainersList() { return trainersList; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public void setAddress(String address) { this.address = address; }

    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public void setTrainersList(List<TrainerInTraineeDTO> trainersList) { this.trainersList = trainersList; }
}
