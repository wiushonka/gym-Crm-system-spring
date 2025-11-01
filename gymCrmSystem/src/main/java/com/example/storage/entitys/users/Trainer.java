package com.example.storage.entitys.users;

import com.example.storage.entitys.training.Training;
import com.example.storage.entitys.training.TrainingType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id")
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Training> trainings;

    public Trainer() {
        user = new User();
    }

    public Trainer(User user, TrainingType specialization) {
        this.user = user;
        this.specialization = specialization;
        trainees = new ArrayList<>();
        trainings = new ArrayList<>();
    }

    public Long getId() { return id;}

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Trainee> getTrainees() { return trainees; }

    public void setTrainees(List<Trainee> traines) { this.trainees = traines; }

    public TrainingType getSpecialization() { return specialization; }

    public void setSpecialization(TrainingType specialization) { this.specialization = specialization; }

    public void removeTrainee(Trainee trainee) { this.trainees.remove(trainee); }

    public List<Training> getTrainings() { return trainings; }

    public void setTrainings(List<Training> trainings) { this.trainings = trainings; }

    public void addTraining(Training training) {
        if(this.trainings.contains(training)) return;
        trainings.add(training);
        training.setTrainer(this);
    }

    public void addTrainee(Trainee trainee) {
        if (!trainees.contains(trainee)) {
            trainees.add(trainee);
            trainee.getTrainers().add(this);
        }
    }

    public String getUserName() { return user.getUserName(); }

    public void setUserName(String userName) { this.user.setUserName(userName); }

    public String getPassword() { return user.getPassword(); }

    public void setPassword(String password) { this.user.setPassword(password); }

    public String getFirstName() { return user.getFirstName(); }

    public void setFirstName(String firstName) { this.user.setFirstName(firstName); }

    public String getLastName() { return user.getLastName(); }

    public void setLastName(String lastName) { this.user.setLastName(lastName); }

    public Boolean isActive(){ return this.user.isActive() ;}

    public void setActive(Boolean active){ this.user.setActive(active);}

    public void removeTraining(Training training) { this.trainings.remove(training); }

    public String getSpecializationName() { return this.specialization.getTrainingTypeName(); }
}
