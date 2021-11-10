package com.example.caloriestracker;

public class Workout {

    private Double MET;
    private String stepName;
    private String stepDesc;
    private double time;

    public Workout(Double MET, String stepName, String stepDesc, double time) {
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
