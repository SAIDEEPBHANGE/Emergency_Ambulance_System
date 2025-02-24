package com.project.ambulancecall.driver;

public class DriverData {
    String Email;
    String FullName;
    String Key;
    String MobileNumber;
    String AmbulanceNumber;
    String AadharNumber;



    public DriverData() {
    }

    public DriverData(String email, String fullName, String key, String mobileNumber,String ambulanceNumber,String aadharNumber) {
        Email = email;
        FullName = fullName;
        Key = key;
        MobileNumber = mobileNumber;
        AmbulanceNumber = ambulanceNumber;
        AadharNumber = aadharNumber;
    }

    public String getAadharNumber() {
        return AadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        AadharNumber = aadharNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getAmbulanceNumber() {
        return AmbulanceNumber;
    }

    public void setAmbulanceNumber(String ambulanceNumber) {
        AmbulanceNumber = ambulanceNumber;
    }
}
