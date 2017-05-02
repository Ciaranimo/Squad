package com.ciaranbyrne.squad;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ciaranbyrne on 04/04/2017.
 */

public class Group {

    private String groupId;
    private String adminId;
    private Map<String, Boolean> members = new HashMap<>();

    public Group() {

    }

    public Group(String groupId, String adminId) {
        this.groupId = groupId;
        this.adminId = adminId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }


}



