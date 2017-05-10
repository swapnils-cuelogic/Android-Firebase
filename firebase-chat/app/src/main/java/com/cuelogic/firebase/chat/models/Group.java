package com.cuelogic.firebase.chat.models;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public class Group {
    public String roomId;
    public String displayName;
    public long createdTime;
    public String createdBy;
    public long lastUpdatedTime;
    public List<String> users;

    public Group() {
    }

    public Group(String roomId, String displayName, long createdTime, String createdBy, long lastUpdatedTime, List<String> users) {
        this.roomId = roomId;
        this.displayName = displayName;
        this.createdTime = createdTime;
        this.createdBy = createdBy;
        this.lastUpdatedTime = lastUpdatedTime;
        this.users = users;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
