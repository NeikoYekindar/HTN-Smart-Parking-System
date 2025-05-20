package com.example.parking_app;

public class ParkingSpot {
    String spotID;
    Boolean isOccupied;
    Boolean isLocked;
    Boolean isReported;

    public ParkingSpot(String spotID, Boolean isOccupied, Boolean isLocked, Boolean isReported) {
        this.spotID = spotID;
        this.isOccupied = isOccupied;
        this.isLocked = isLocked;
        this.isReported = isReported;
    }

    public String getSpotID(){
        return spotID;
    }
    public void setSpotID(String spotID){
        this.spotID = spotID;
    }
    public Boolean getIsOccupied(){
        return isOccupied;
    }
    public void setIsOccupied(Boolean isOccupied){
        this.isOccupied = isOccupied;
    }
    public Boolean getIsLocked(){
        return isLocked;
    }
    public void setIsLocked(Boolean isLocked){
        this.isLocked = isLocked;
    }
    public Boolean getIsReported(){
        return isReported;
    }
    public void setIsReported(Boolean isReport){
        this.isReported = isReport;
    }
}
