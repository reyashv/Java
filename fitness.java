package com.example.jproject;

public class fitness {
    private int GoalID;
    private String GoalType;
    private double TargetVal;
    private String TargetDate;
    private boolean achieved;

    public fitness() {
        GoalID=0;
        GoalType="null";
        TargetVal=30.0;
        TargetDate="01-01-2024";
        achieved=true;
    }

    public int getGoalID() {
        return GoalID;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public double getTargetVal() {
        return TargetVal;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getGoalType() {
        return GoalType;
    }

    public String getTargetDate() {
        return TargetDate;
    }

    public void setGoalType(String goalType) {
        GoalType = goalType;
    }

    public void setGoalID(int goalID) {
        GoalID = goalID;
    }

    public void setTargetDate(String targetDate) {
        TargetDate = targetDate;
    }

    public void setTargetVal(double targetValue) {
        TargetVal = targetValue;
    }
}

