package com.example.smartprescription;

public class Model {

    private String medicine, dosage, duration, id, date;

    public  Model() {

    }

    public Model(String medicine, String dosage, String duration, String id, String date) {
        this.medicine = medicine;
        this.dosage = dosage;
        this.duration = duration;
        this.id = id;
        this.date = date;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
