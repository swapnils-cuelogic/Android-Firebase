package com.cuelogic.firebase.chat.models;

/**
 * Created by Harshal Vibhandik on 12/05/17.
 */

public class RoomDetails {
    public int unreadCount = 0;
    public String lastMessage = "";
    public String lastMessageTime = "";
    public boolean isMuted = false;

    public RoomDetails(int unreadCount, String lastMessage, String lastMessageTime, boolean isMuted) {
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.isMuted = isMuted;
    }

    public RoomDetails() {
    }
}
