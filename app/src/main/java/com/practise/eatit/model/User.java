package com.practise.eatit.model;

public class User {
    private String userName, userPassword, userPhoneNum, email, address,  image;
    private boolean staff;

    public User() {
    }

    public User(String userName, String userPassword, String userPhoneNum, String email, String address, String image, boolean staff) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userPhoneNum = userPhoneNum;
        this.email = email;
        this.address = address;
        this.image = image;
        this.staff = staff;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }
}
