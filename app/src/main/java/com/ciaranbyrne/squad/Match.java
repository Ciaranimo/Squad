package com.ciaranbyrne.squad;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ciaranbyrne on 28/04/2017.
 */

public class Match {

    private String matchTime;
    private String matchDay;
    private int matchNumbers;
    private Boolean evenTeams;
    private Boolean weekly;
    private String groupId;
    private String adminName;
    public Map<String, Boolean> matches = new HashMap<>();


    public Match(String matchTime, String matchDay, int matchNumbers, Boolean evenTeams, Boolean weekly, String groupId, String adminName) {
        this.matchTime = matchTime;
        this.matchDay = matchDay;
        this.matchNumbers = matchNumbers;
        this.evenTeams = evenTeams;
        this.weekly = weekly;
        this.groupId = groupId;
        this.adminName = adminName;
    }

    public Match() {

    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(String matchDay) {
        this.matchDay = matchDay;
    }

    public int getMatchNumbers() {
        return matchNumbers;
    }

    public void setMatchNumbers(int matchNumbers) {
        this.matchNumbers = matchNumbers;
    }

    public Boolean getEvenTeams() {
        return evenTeams;
    }

    public void setEvenTeams(Boolean evenTeams) {
        this.evenTeams = evenTeams;
    }

    public Boolean getWeekly() {
        return weekly;
    }

    public void setWeekly(Boolean weekly) {
        this.weekly = weekly;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setGroups(Map<String, Boolean> matches) {
        this.matches = matches;
    }
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("matchTime", matchTime);
        result.put("matchDay", matchDay);
        result.put("matchNumbers", matchNumbers);
        result.put("evenTeams", evenTeams);
        result.put("weekly", evenTeams);
        result.put("groupId", groupId);
        result.put("adminName", adminName);

        return result;
    }
}
