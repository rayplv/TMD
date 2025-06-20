package com.model;

public class ScoreData {
    private String username;
    private int score;
    private int count;

    // Constructor
    public ScoreData() {}

    // Getters
    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getCount() {
        return count;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCount(int count) {
        this.count = count;
    }
}