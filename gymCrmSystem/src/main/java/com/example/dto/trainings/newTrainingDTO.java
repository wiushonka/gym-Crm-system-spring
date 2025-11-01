package com.example.dto.trainings;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class newTrainingDTO {
    @NotNull
    String traineeUsername;
    @NotNull
    String trainerUsername;
    @NotNull
    String trainingName;
    @NotNull
    Date trainingStartDate;
    @NotNull
    Integer trainingDuration;

    public newTrainingDTO(@NotNull String traineeUsername, @NotNull String trainerUsername,
                          @NotNull String trainingName, @NotNull Date trainingStartDate, @NotNull Integer trainingDuration) {
        this.traineeUsername = traineeUsername;
        this.trainerUsername = trainerUsername;
        this.trainingName = trainingName;
        this.trainingStartDate = trainingStartDate;
        this.trainingDuration = trainingDuration;
    }

    public @NotNull String getTraineeUsername() { return traineeUsername; }

    public @NotNull String getTrainerUsername() { return trainerUsername; }

    public @NotNull String getTrainingName() { return trainingName; }

    public @NotNull Date getTrainingStartDate() { return trainingStartDate; }

    public @NotNull Integer getTrainingDuration() { return trainingDuration; }

    public void setTraineeUsername(@NotNull String traineeUsername)  { this.traineeUsername = traineeUsername; }

    public void setTrainerUsername(@NotNull String trainerUsername)  { this.trainerUsername = trainerUsername; }

    public void setTrainingName(@NotNull String trainingName)  { this.trainingName = trainingName; }

    public void setTrainingStartDate(@NotNull Date trainingStartDate)  { this.trainingStartDate = trainingStartDate; }

    public void setTrainingDuration(@NotNull Integer trainingDuration)  { this.trainingDuration = trainingDuration; }
}
