package com.dd.menyoo.model;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class RestaurantModel {
    private int restaurantID;
    private String restaurantName;
    private String imageUrl;
    private boolean isTableOccupied = false;
    private boolean isRestaurantActive;
    private boolean isCommingSoon = false;
    private String isCommingSoonMessage;
    private String feedbackEmail;
    private boolean isReadOnly;
    private String loyalityDetails;
    private String loyalityRequirements;
    private boolean isLoyaltyFeatureActive;
    private double gst;
    private double seviceTax;

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isTableOccupied() {
        return isTableOccupied;
    }

    public boolean isRestaurantActive() {
        return isRestaurantActive;
    }

    public void setRestaurantActive(boolean restaurantActive) {
        isRestaurantActive = restaurantActive;
    }

    public boolean isLoyaltyFeatureActive() {
        return isLoyaltyFeatureActive;
    }

    public boolean isCommingSoon() {
        return isCommingSoon;
    }

    public String getIsCommingSoonMessage() {
        return isCommingSoonMessage;
    }

    public String getFeedbackEmail() {
        return feedbackEmail;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public String getLoyalityDetails() {
        return loyalityDetails;
    }

    public String getLoyalityRequirements() {
        return loyalityRequirements;
    }

    public double getGst() {
        return gst;
    }

    public double getSeviceTax() {
        return seviceTax;
    }

    public RestaurantModel(int restaurantID, String restaurantName, String imageUrl) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
    }

    public RestaurantModel(int restaurantID, String restaurantName,
                           String imageUrl, boolean isTableOccupied, boolean isRestaurantActive) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
    }


    public RestaurantModel(int restaurantID, String restaurantName,
                           String imageUrl, boolean isTableOccupied, boolean isRestaurantActive,
                           boolean isCommingSoon, String isCommingSoonMessage) {
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
                           boolean isRestaurantActive, String feedbackEmail, boolean isReadOnly) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
        this.feedbackEmail = feedbackEmail;
        this.isReadOnly = isReadOnly;
    }

    public RestaurantModel(int restaurantID, String restaurantName,
                           String imageUrl, boolean isTableOccupied,
                           boolean isRestaurantActive, String feedbackEmail,
                           String loyalityRequirements, String loyalityDetails,
                           boolean isReadOnly, boolean isLoyaltyFeatureActive,
                           double gst, double seviceTax) {
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.imageUrl = imageUrl;
        this.isTableOccupied = isTableOccupied;
        this.isRestaurantActive = isRestaurantActive;
        this.feedbackEmail = feedbackEmail;
        this.loyalityRequirements = loyalityRequirements;
        this.loyalityDetails = loyalityDetails;
        this.isReadOnly = isReadOnly;
        this.isLoyaltyFeatureActive = isLoyaltyFeatureActive;
        this.gst = gst;
        this.seviceTax = seviceTax;

    }


}
