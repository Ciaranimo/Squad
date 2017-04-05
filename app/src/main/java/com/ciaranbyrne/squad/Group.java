package com.ciaranbyrne.squad;

import java.util.List;

/**
 * Created by ciaranbyrne on 04/04/2017.
 */

public class Group {

    public String groupId;
    public String groupName;
    // public List <User> memberList;

    public Group(){

    }

    public Group(String groupId, List<User> members) {
        this.groupId = groupId;
        //this.memberList = members;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<User> getMembers() {
        return memberList;
    }

    public void setMembers(User member) {
        this.memberList.add(member);
    }
};



