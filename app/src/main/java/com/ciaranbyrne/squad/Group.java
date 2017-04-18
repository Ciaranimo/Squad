package com.ciaranbyrne.squad;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ciaranbyrne on 04/04/2017.
 */

public class Group {

    private String groupId;
    private String memberId;
    private Map<String, Boolean> members = new HashMap<>();

    public Group() {

    }

    public Group(String groupId, String memberId) {
        this.groupId = groupId;
        this.memberId = memberId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMember() {
        return memberId;
    }

    public void setMember(String memberId) {
        this.memberId = memberId;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }


}



