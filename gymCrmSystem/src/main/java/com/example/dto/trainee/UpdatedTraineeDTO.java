package com.example.dto.trainee;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Optional;

public class UpdatedTraineeDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private Date dateOfBirth;
    private String address;
    @NotNull
    private Boolean isActive;

    public UpdatedTraineeDTO(@NotNull String firstName, @NotNull String lastName,
                             Date dateOfBirth, String address, @NotNull Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.isActive = isActive;
    }


    public @NotNull String getFirstName() { return firstName; }

    public @NotNull String getLastName() { return lastName; }

    public Optional<Date> getDateOfBirth() { return Optional.ofNullable(dateOfBirth); }

    public Optional<String> getAddress() { return Optional.ofNullable(address); }

    public @NotNull Boolean isActive() { return isActive; }

    public void setFirstName(@NotNull String firstName) { this.firstName = firstName; }

    public void setLastName(@NotNull String lastName) { this.lastName = lastName; }

    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public void setAddress(String address) { this.address = address; }

    public void setIsActive(@NotNull Boolean isActive) { this.isActive = isActive; }
}
