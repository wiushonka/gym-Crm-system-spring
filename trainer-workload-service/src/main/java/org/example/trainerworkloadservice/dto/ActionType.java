package org.example.trainerworkloadservice.dto;

public enum ActionType {
    ADD,DELETE;

    public static ActionType fromString(String str) {
        if(str==null || str.isEmpty() || (!str.equalsIgnoreCase("ADD") && !str.equalsIgnoreCase("DELETE"))) {
            throw new IllegalArgumentException("Invalid ActionType: " + str);
        }
        return ActionType.valueOf(str.toUpperCase());
    }
}
