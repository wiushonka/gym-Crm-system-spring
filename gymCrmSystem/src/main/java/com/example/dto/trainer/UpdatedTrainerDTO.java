package com.example.dto.trainer;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.ReadOnlyProperty;

public class UpdatedTrainerDTO {
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    @ReadOnlyProperty
    String specializationName;
    @NotNull
    Boolean isActive;

    public UpdatedTrainerDTO(@NotNull String firstName, @NotNull String lastName, String specializationName, @NotNull Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specializationName = specializationName;
        this.isActive = isActive;
    }

    public @NotNull String getFirstName() { return firstName; }

    public @NotNull String getLastName() { return lastName; }

    public String getSpecializationName() { return specializationName; }

    public @NotNull Boolean getIsActive() { return isActive; }

    public void setFirstName(@NotNull String firstName) { this.firstName = firstName; }

    public void setLastName(@NotNull String lastName) { this.lastName = lastName; }

    public void setIsActive(@NotNull Boolean isActive) { this.isActive = isActive; }

    public void setSpecializationName(String specializationName) { this.specializationName = specializationName; }
}
