package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 05/03/2017.
 */

public class User {
    private String name;
    private String email;


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public User() {

    }

    public String getUid() {
        return email;
    }

    public void setUid(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }




}
