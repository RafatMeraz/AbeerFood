package com.practise.eatit.model;

public class Rating {
    private String phone;
    private String foodId;
    private String rateValue;
    private String comment;
    private String email;

    public Rating(){}

    public Rating(String phone, String foodId, String rateValue, String comment, String email) {
        this.phone = phone;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
