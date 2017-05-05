package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class User {
    private String uId;
    private String name;
    private String email;
    private String phoneNum;
    private String searchNum;


    public User(String uId, String name, String email, String phoneNum, String searchNum) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
        this.searchNum = searchNum;
    }
    public User() {

    }

    public String getUid() {
        return uId;
    }

    public void setUid(String uId) {
        this.uId = uId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSearchNum() {
        return searchNum;
    }

    public void setSearchNum(String searchNum) {
        this.searchNum = searchNum;
    }
}
