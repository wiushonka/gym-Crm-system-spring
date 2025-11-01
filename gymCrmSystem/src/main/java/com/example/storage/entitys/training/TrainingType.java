package com.example.storage.entitys.training;

import jakarta.persistence.*;

@Entity
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trainingTypeName;

    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() { return id; }

    public String getTrainingTypeName() { return trainingTypeName; }

    public void setTrainingTypeName(String trainingTypeName) { this.trainingTypeName = trainingTypeName; }
}
