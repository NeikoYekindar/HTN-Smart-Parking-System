package com.example.parking_app.admin;

public class Admin {
    private String adminID;
    public Admin(String adminID){
        this.adminID = adminID;
    }
    public String getAdminID(){
        return adminID;
    }
    public void setAdminID(String adminID){
        this.adminID = adminID;
    }
}
