package com.ciaranbyrne.squad;

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

    public Match(String matchTime, String matchDay, int matchNumbers, Boolean evenTeams, Boolean weekly, String groupId) {
        this.matchTime = matchTime;
        this.matchDay = matchDay;
        this.matchNumbers = matchNumbers;
        this.evenTeams = evenTeams;
        this.weekly = weekly;
        this.groupId = groupId;
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
}
