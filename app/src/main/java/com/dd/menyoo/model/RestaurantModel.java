package com.dd.menyoo.model;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class RestaurantModel {
    private int restaurantID;
    private String restaurantName ;
    private String imageUrl;
    private boolean isTableOccupied=false ;
    private boolean isRestaurantActive;
    private boolean isCommingSoon =false ;
    private String isCommingSoonMessage;
    private String feedbackEmail;
    private boolean isReadOnly;

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isTableOccupied() {
        return isTableOccupied;
    }

    public void setTableOccupied(boolean tableOccupied) {
        isTableOccupied = tableOccupied;
    }

    public boolean isRestaurantActive() {
        return isRestaurantActive;
    }

    public void setRestaurantActive(boolean restaurantActive) {
        isRestaurantActive = restaurantActive;
    }

    public RestaurantModel(int restaurantID, String restaurantName, String imageUrl) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
    }

    public RestaurantModel(int restaurantID, String restaurantName, String imageUrl, boolean isTableOccupied,boolean isRestaurantActive) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
    }



    public RestaurantModel(int restaurantID, String restaurantName, String imageUrl, boolean isTableOccupied, boolean isRestaurantActive, boolean isCommingSoon, String isCommingSoonMessage) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
        this.isCommingSoon = isCommingSoon;
        this.isCommingSoonMessage = isCommingSoonMessage;
    }

    public RestaurantModel(int restaurantID, String restaurantName,
                           String imageUrl, boolean isTableOccupied,
                           boolean isRestaurantActive ,String feedbackEmail, boolean isReadOnly) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
        this.feedbackEmail = feedbackEmail;
        this.isReadOnly = isReadOnly;
    }

    public boolean isCommingSoon() {
        return isCommingSoon;
    }

    public void setCommingSoon(boolean commingSoon) {
        isCommingSoon = commingSoon;
    }

    public String getIsCommingSoonMessage() {
        return isCommingSoonMessage;
    }

    public void setIsCommingSoonMessage(String isCommingSoonMessage) {
        this.isCommingSoonMessage = isCommingSoonMessage;
    }

    public String getFeedbackEmail() {
        return feedbackEmail;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }
}
