package com.example.storage.entitys.users;

import com.example.storage.entitys.training.Training;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date dateOfBirth;

    private String address;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers;

    public Trainee() {
        user = new User();
        user.setActive(true);
    }

    @OneToMany(mappedBy = "trainee", cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private List<Training> trainings;

    public Trainee(Date dateOfBirth, String address, User user) {
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.user = user;
        trainers = new ArrayList<>();
        trainings = new ArrayList<>();
    }

    public Long getId() { return id; }

    public Date getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Trainer> getTrainers() { return trainers; }

    public void setTrainers(List<Trainer> trainers) { this.trainers = trainers; }

    public List<Training> getTrainings() { return trainings; }

    public void setTrainings(List<Training> trainings) { this.trainings = trainings; }

    public void removeTrainer(Trainer trainer) { this.trainers.remove(trainer); }

    public void addTraining(Training training) {
        if(this.trainings.contains(training)) return;
        trainings.add(training);
        training.setTrainee(this);
    }

    public void addTrainer(Trainer trainer) {
        if (!trainers.contains(trainer)) {
            trainers.add(trainer);
            trainer.getTrainees().add(this);
        }
    }

    public String getUserName() { return user.getUserName(); }

    public void setUserName (String userName) { this.user.setUserName(userName); }

    public String getPassword() { return user.getPassword(); }

    public void setPassword(String password) { this.user.setPassword(password); }

    public String getFirstName() { return user.getFirstName(); }

    public void setFirstName(String firstName) { this.user.setFirstName(firstName); }

    public String getLastName() { return user.getLastName(); }

    public void setLastName(String lastName) { this.user.setLastName(lastName); }

    public Boolean isActive(){ return this.user.isActive() ;}

    public void setActive(Boolean active){ this.user.setActive(active);}

    public void removeTraining(Training training) { this.trainings.remove(training); }
}
