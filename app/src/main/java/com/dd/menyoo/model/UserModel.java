package com.dd.menyoo.model;

/**
 * Created by Administrator on 22-Feb-16.
 */
public class UserModel {
    int userId;
    String firstName;
    String lastName;
    String emailAddress;
    String phoneNumber;
    String displayPicturePAth;
    boolean isGuest;

    public UserModel(int userId, String firstName, String lastName, String emailAddress, String phoneNumber, String displayPicturePAth) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.displayPicturePAth = displayPicturePAth;
        this.isGuest = false;
    }
    public UserModel(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayPicturePAth() {
        return displayPicturePAth;
    }

    public void setDisplayPicturePAth(String displayPicturePAth) {
        this.displayPicturePAth = displayPicturePAth;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }
}
