package com.example.dto.trainer;

import org.jetbrains.annotations.NotNull;

public class TrainerRegistrationDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String specialization;

    public TrainerRegistrationDTO(@NotNull String firstName, @NotNull String lastName, @NotNull String specialization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    public @NotNull String getFirstName() { return firstName; }

    public @NotNull String getLastName() { return lastName; }

    public @NotNull String getSpecialization() { return specialization; }

    public void setSpecialization(@NotNull String specialization) { this.specialization = specialization; }

    public void setFirstName(@NotNull String firstName) { this.firstName = firstName; }

    public void setLastName(@NotNull String lastName) { this.lastName = lastName; }
}
