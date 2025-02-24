package com.project.ambulancecall.driver;

public class RequestData {
    String UserName,UserNumber,Source_Lon,Source_Lat,Destination,AmbulanceType,Status,Key,AmbulanceNumber,DriverName,DriverNumber,Ammount,DateTime;

    public RequestData(String userName, String userNumber, String source_Lon, String source_Lat, String destination, String ambulanceType, String status, String key, String ambulanceNumber, String driverName, String driverNumber,String ammount,String dateTime) {
        UserName = userName;
        UserNumber = userNumber;
        Source_Lon = source_Lon;
        Source_Lat = source_Lat;
        Destination = destination;
        AmbulanceType = ambulanceType;
        Status = status;
        Key = key;
        AmbulanceNumber = ambulanceNumber;
        DriverName = driverName;
        DriverNumber = driverNumber;
        Ammount = ammount;
        DateTime = dateTime;
    }

    public RequestData() {
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getSource_Lon() {
        return Source_Lon;
    }

    public void setSource_Lon(String source_Lon) {
        Source_Lon = source_Lon;
    }

    public String getSource_Lat() {
        return Source_Lat;
    }

    public void setSource_Lat(String source_Lat) {
        Source_Lat = source_Lat;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getAmbulanceType() {
        return AmbulanceType;
    }

    public void setAmbulanceType(String ambulanceType) {
        AmbulanceType = ambulanceType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getAmbulanceNumber() {
        return AmbulanceNumber;
    }

    public void setAmbulanceNumber(String ambulanceNumber) {
        AmbulanceNumber = ambulanceNumber;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverNumber() {
        return DriverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        DriverNumber = driverNumber;
    }

    public String getAmmount() {
        return Ammount;
    }

    public void setAmmount(String ammount) {
        Ammount = ammount;
    }
}
