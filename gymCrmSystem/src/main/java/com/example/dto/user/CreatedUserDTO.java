package com.example.dto.user;

public class CreatedUserDTO {
    private String username;
    private String password;
    private String bearerToken;

    public CreatedUserDTO(String username, String password, String bearerToken) {
        this.username = username;
        this.password = password;
        this.bearerToken = bearerToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBearerToken() { return bearerToken; }

    public void setBearerToken(String bearerToken) { this.bearerToken = bearerToken; }
}
