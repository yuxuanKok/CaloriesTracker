package com.example.caloriestracker;

import com.google.type.DateTime;

import java.io.Serializable;

public class Food implements Serializable {
    String foodName;
    int qty;
    DateTime dateTime;
    int totalCal;
    boolean healthy;

    public Food(){}

    public Food(String foodName, int totalCal, int qty, boolean healthy, DateTime dateTime) {
        this.foodName = foodName;
        this.totalCal = totalCal;
        this.qty = qty;
        this.healthy = healthy;
        this.dateTime = dateTime;
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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
