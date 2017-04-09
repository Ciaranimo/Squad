package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class User {
    private String uid;
    private String name;

    public User(String uid, String name) {
        this.name = name;
        this.uid = uid;
    }
    public User() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }




}
