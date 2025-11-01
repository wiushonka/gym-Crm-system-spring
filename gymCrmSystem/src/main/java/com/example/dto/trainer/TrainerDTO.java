package com.example.dto.trainer;

import java.util.List;

public class TrainerDTO {
    String firstname;
    String lastname;
    String specializationName;
    Boolean isActive;
    List<TraineesInTrainerDTO> traineesList;

    public TrainerDTO() {}

    public String getFirstname() { return firstname; }

    public String getLastname() { return lastname; }

    public String getSpecializationName() { return specializationName; }

    public Boolean getIsActive() { return isActive; }

    public List<TraineesInTrainerDTO> getTraineesList() { return traineesList; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public void setTraineesList(List<TraineesInTrainerDTO> lst) { traineesList = lst; }

    public void setSpecializationName(String specializationName) { this.specializationName = specializationName; }
}
