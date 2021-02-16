package com.app.knn;

public class CompareObject {
    private double value;
    private String Drug;

    public CompareObject(double value, String Drug){
        this.value = value;
        this.Drug = Drug;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDrug() {
        return Drug;
    }

    public void setDrug(String drug) {
        Drug = drug;
    }
}
