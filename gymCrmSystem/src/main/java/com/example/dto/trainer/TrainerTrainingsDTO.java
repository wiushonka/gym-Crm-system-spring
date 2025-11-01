package com.example.dto.trainer;

import java.util.Date;

public class TrainerTrainingsDTO {
    private String name;
    private Date startDate;
    private String type;
    private Integer duration;
    private String traineeUsername;

    public TrainerTrainingsDTO(String name , Date startDate , String type , Integer duration , String traineeUsername) {
        this.name = name;
        this.startDate = startDate;
        this.type = type;
        this.duration = duration;
        this.traineeUsername = traineeUsername;
    }

    public String getName() { return name; }

    public Date getStartDate() { return startDate; }

    public String getType() { return type; }

    public Integer getDuration() { return duration; }

    public String getTraineeUsername() { return traineeUsername; }

    public void setName(String name) { this.name = name; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public void setType(String type) { this.type = type; }

    public void setDuration(Integer duration) { this.duration = duration; }

    public void setTraineeUsername(String traineeUsername) { this.traineeUsername = traineeUsername; }
}
