package com.project.ambulancecall.user;

import android.content.Context;

public class UserData {

    String Email;
    String FullName;
    String Key;
    String MobileNumber;
    String AadharNumber;

    public UserData() {
    }

    public UserData(String email, String fullName, String key, String mobileNumber,String aadharNumber) {
        Email = email;
        FullName = fullName;
        Key = key;
        MobileNumber = mobileNumber;
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

    public String getAadharNumber() {
        return AadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        AadharNumber = aadharNumber;
    }
}
