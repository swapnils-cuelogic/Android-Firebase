package com.cuelogic.firebase.chat.models;

/**
 * Created by Harshal Vibhandik on 12/05/17.
 */

public class RoomDetails {
    public int unreadCount = 0;
    public String lastMessageTime = "";
    public boolean isMuted = false;

    public RoomDetails(int unreadCount, String lastMessageTime, boolean isMuted) {
        this.unreadCount = unreadCount;
        this.lastMessageTime = lastMessageTime;
        this.isMuted = isMuted;
    }

    public RoomDetails() {
    }
}
