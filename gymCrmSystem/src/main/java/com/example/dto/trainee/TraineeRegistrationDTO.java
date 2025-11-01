package com.example.dto.trainee;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Optional;

public class TraineeRegistrationDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private Date birthDate;
    private String address;

    public TraineeRegistrationDTO(@NotNull String firstName, @NotNull String lastName, Date birthDate, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
    }

    public @NotNull String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull String firstName) {
        this.firstName = firstName;
    }

    public @NotNull String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull String lastName) {
        this.lastName = lastName;
    }

    public Optional<Date> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Optional<String> getAddress() {
        return Optional.ofNullable(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
