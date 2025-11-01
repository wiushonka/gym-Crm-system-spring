package com.example.dto.trainings;

public class TrainingTypeDTO {
    String trainingType;
    Long id;

    public TrainingTypeDTO(String trainingType, Long id) {
        this.trainingType = trainingType;
        this.id = id;
    }

    public String getTrainingType() { return trainingType; }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }
}
