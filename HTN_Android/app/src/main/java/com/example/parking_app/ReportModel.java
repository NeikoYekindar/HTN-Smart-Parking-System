package com.example.parking_app;

import java.util.Date;

public class ReportModel {
    String name;
    String email;
    String phone_number;
    String date;
    String parking;
    String details;
    Boolean isCheck;

    public ReportModel(String name, String email, String phone_number, String date, String parking, String details, Boolean isCheck) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.details = details;
        this.date = date;
        this.parking = parking;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getDetails() {
        return details;
    }

    public String getDate() {
        return date;
    }

    public String getParking() {
        return parking;
    }

    public Boolean getIsCheck(){return isCheck;}
}
