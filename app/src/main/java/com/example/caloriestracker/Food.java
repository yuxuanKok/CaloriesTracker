package com.example.caloriestracker;

import java.io.Serializable;
import java.util.Date;

public class Food implements Serializable {
    String foodName;
    int qty;
    Date dateTime;
    int totalCal;
    boolean healthy;
    String reason;

    public Food(){}

    public Food(String foodName, int totalCal, int qty, boolean healthy, Date dateTime, String reason) {
        this.foodName = foodName;
        this.totalCal = totalCal;
        this.qty = qty;
        this.healthy = healthy;
        this.dateTime = dateTime;
        this.reason = reason;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getTotalCal() {
        return totalCal;
    }

    public void setTotalCal(int totalCal) {
        this.totalCal = totalCal;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
