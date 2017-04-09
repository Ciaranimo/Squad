package com.ciaranbyrne.squad;

/**
 * Created by ciaranbyrne on 08/04/2017.
 */

public class Player {

    private String name;
    private String uid;

    public Player(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
