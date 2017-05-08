package com.ciaranbyrne.squad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ciaranbyrne on 08/04/2017.
 */

public class Player {

    public String name;
    public Boolean playingExtra;
    public String groupId;
    public String phoneNum;
    public Map<String, Boolean> groups = new HashMap<>();


    public Player( String name, Boolean playingExtra, String groupId, String phoneNum) {
        this.name = name;

        this.playingExtra = playingExtra;
        this.groupId = groupId;
        this.phoneNum = phoneNum;
    }

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Boolean getPlayingExtra() {
        return playingExtra;
    }

    public void setPlayingExtra(Boolean playingExtra) {
        this.playingExtra = playingExtra;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }


    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("playingExtra", playingExtra);
        result.put("groupId", groupId);
        result.put("phoneNum", phoneNum);


        return result;
    }
}
