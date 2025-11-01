package com.example.dto.trainee;

import java.util.Date;

public class TraineeTrainingsDTO {
    private String name;
    private Date startDate;
    private String type;
    private Integer duration;
    private String trainerUsername;

    public TraineeTrainingsDTO(String name , Date startDate , String type , Integer duration , String trainerUsername) {
        this.name = name;
        this.startDate = startDate;
        this.type = type;
        this.duration = duration;
        this.trainerUsername = trainerUsername;
    }

    public String getName() { return name; }

    public Date getStartDate() { return startDate; }

    public String getType() { return type; }

    public Integer getDuration() { return duration; }

    public String getTrainerUsername() { return trainerUsername; }

    public void setName(String name) { this.name = name; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public void setType(String type) { this.type = type; }

    public void setDuration(Integer duration) { this.duration = duration; }

    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }
}
