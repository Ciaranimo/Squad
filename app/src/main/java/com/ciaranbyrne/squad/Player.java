package com.ciaranbyrne.squad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ciaranbyrne on 08/04/2017.
 */

public class Player {

    public String name;
    public Boolean playing;
    public String groupId;
    public String phoneNum;
    public String searchNum;
    public Map<String, Boolean> groups = new HashMap<>();


    public Player( String name, Boolean playing, String groupId, String phoneNum, String searchNum) {
        this.name = name;

        this.playing = playing;
        this.groupId = groupId;
        this.phoneNum = phoneNum;
        this.searchNum = searchNum;
    }

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Boolean getPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
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

    public String getSearchNum() {
        return searchNum;
    }

    public void setSearchNum(String searchNum) {
        this.searchNum = searchNum;
    }

    public void setGroups(Map<String, Boolean> groups) {
        this.groups = groups;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("playing", playing);
        result.put("groupId", groupId);
        result.put("phoneNum", phoneNum);
        result.put("searchNum", searchNum);

        return result;
    }
}
