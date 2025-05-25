package com.Trongtoan.ProjGame.logic;

public class Mission {
    private final String id;
    private final String description;
    private boolean isCompleted = false;

    public Mission(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public void complete() {
        isCompleted = true;
    }

    public void reset() {
        isCompleted = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
}
