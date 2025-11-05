package org.example.trainerworkloadservice.dto;

public enum ActionType {
    ADD,DELETE,SUMMARY;

    public static ActionType fromString(String str) {
        if(str==null || str.isEmpty() || (!str.equalsIgnoreCase("ADD") && !str.equalsIgnoreCase("DELETE")
        && !str.equalsIgnoreCase("SUMMARY"))) {
            throw new IllegalArgumentException("Invalid ActionType: " + str);
        }
        return ActionType.valueOf(str.toUpperCase());
    }
}
