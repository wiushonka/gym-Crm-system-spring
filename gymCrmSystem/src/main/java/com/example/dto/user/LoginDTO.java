package com.example.dto.user;

import org.jetbrains.annotations.NotNull;

public class LoginDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;

    public LoginDTO(@NotNull String userName, @NotNull String password) {
        this.username = userName;
        this.password = password;
    }

    public @NotNull String getUserName() { return username; }

    public @NotNull String getPassword() { return password; }

    public void setUserName(@NotNull String userName) { this.username = userName; }

    public void setPassword(@NotNull String password) { this.password = password; }
}
