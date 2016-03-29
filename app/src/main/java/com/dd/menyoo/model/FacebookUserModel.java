package com.dd.menyoo.model;

/**
 * Created by Administrator on 26-Feb-16.
 */
public class FacebookUserModel extends BaseClassModel {
   private String firstName ;
   private String lastName ;
   private String userID;
   private String proficPic ;
   private String accessToken ;
   private String email;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProficPic() {
        return proficPic;
    }

    public void setProficPic(String proficPic) {
        this.proficPic = proficPic;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FacebookUserModel(String firstName, String lastName, String userID, String proficPic, String accessToken, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.proficPic = proficPic;
        this.accessToken = accessToken;
        this.email = email;
    }
}
