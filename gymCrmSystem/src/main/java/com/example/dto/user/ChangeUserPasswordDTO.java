package com.example.dto.user;

import org.jetbrains.annotations.NotNull;

public class ChangeUserPasswordDTO {
    @NotNull
    private String username;
    @NotNull
    private String newPassword;

    public ChangeUserPasswordDTO(String username, String newPassword) {
       this.username = username;
       this.newPassword = newPassword;
    }

    public @NotNull String getNewPassword() { return newPassword; }

    public @NotNull String getUsername() { return username; }

    public void setNewPassword(@NotNull String newPassword) { this.newPassword = newPassword; }

    public void setUsername(@NotNull String username) { this.username = username; }
}
