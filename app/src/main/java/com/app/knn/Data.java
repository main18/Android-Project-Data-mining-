package com.app.knn;

import static java.lang.Integer.parseInt;

public class Data {
    public int age;
    private String sex;
    private String BP;
    private String cholesterol;
    private double Na;
    private double K;
    private String Drug;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBP() {
        return BP;
    }

    public void setBP(String BP) {
        this.BP = BP;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public double getNa() {
        return Na;
    }

    public void setNa(double na) {
        Na = na;
    }

    public double getK() {
        return K;
    }

    public void setK(double k) {
        K = k;
    }

    public String getDrug() {
        return Drug;
    }

    public void setDrug(String drug) {
        Drug = drug;
    }
}
