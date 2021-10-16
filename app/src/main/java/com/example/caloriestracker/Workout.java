package com.example.caloriestracker;

public class Workout {

    private Double MET;
    private String stepName;
    private String stepDesc;
    private int time;

    public Workout(Double MET, String stepName, String stepDesc, int time) {
        this.MET = MET;
        this.stepName = stepName;
        this.stepDesc = stepDesc;
        this.time = time;
    }

    public Double getMET() {
        return MET;
    }

    public void setMET(Double MET) {
        this.MET = MET;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDesc() {
        return stepDesc;
    }

    public void setStepDesc(String stepDesc) {
        this.stepDesc = stepDesc;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
