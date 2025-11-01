package com.example.dto.trainings;

import java.util.Date;
import java.util.Optional;

public class TrainingsDTO {
    private Date fromDate;
    private Date toDate;
    private String trainerName;

    public TrainingsDTO() {}

    public Optional<Date> getFromDate() { return Optional.ofNullable(fromDate); }

    public void setFromDate(Date fromDate) { this.fromDate = fromDate; }

    public Optional<Date> getToDate() { return Optional.ofNullable(toDate); }

    public void setToDate(Date toDate) { this.toDate = toDate; }

    public Optional<String> getTrainerName() { return Optional.ofNullable(trainerName); }

    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
}
