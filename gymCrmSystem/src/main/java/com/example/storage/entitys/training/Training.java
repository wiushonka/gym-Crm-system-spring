package com.example.storage.entitys.training;

import com.example.storage.entitys.users.Trainee;
import com.example.storage.entitys.users.Trainer;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Date trainingDate;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    public Training() {}

    public Training(String trainingName, Date startDate, Integer duration,
                    Trainee trainee, TrainingType trainingType, Trainer trainer) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainer = trainer;
        this.trainingDate = startDate;
        this.duration = duration;
        this.trainee = trainee;
    }

    public Long getId() { return id; }

    public Integer getDuration() { return duration; }

    public void setDuration(Integer duration) { this.duration = duration; }

    public Date getTrainingDate() { return trainingDate; }

    public void setTrainingDate(Date startDate) { this.trainingDate = startDate; }

    public String getTrainingName() { return trainingName; }

    public void setTrainingName(String trainingName) { this.trainingName = trainingName; }

    public TrainingType getTrainingType() { return trainingType; }

    public void setTrainingType(TrainingType TrainingType) { this.trainingType = TrainingType; }

    public Trainer getTrainer() { return trainer; }

    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Trainee getTrainee() { return trainee; }

    public void setTrainee(Trainee trainee) { this.trainee = trainee; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Training training)) return false;
        return trainingName != null && trainingName.equals(training.trainingName);
    }

    @Override
    public int hashCode() {
        return trainingName != null ? trainingName.hashCode() : 0;
    }
}
