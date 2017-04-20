package com.cuelogic.firebase.chat.database.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @version 1.0.0
 * @Date 20/04/2017
 */
@Entity
public class ChatInfo {

    @Id(autoincrement = true)
    private Long id;
    private String sender;
    private String receiver;
    private String senderUid;
    private String receiverUid;
    private String message;
    @Unique
    private long timestamp;
    private String channelName;
    @Generated(hash = 309373041)
    public ChatInfo(Long id, String sender, String receiver, String senderUid,
            String receiverUid, String message, long timestamp,
            String channelName) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.channelName = channelName;
    }
    @Generated(hash = 600262715)
    public ChatInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSender() {
        return this.sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return this.receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getSenderUid() {
        return this.senderUid;
    }
    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
    public String getReceiverUid() {
        return this.receiverUid;
    }
    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getChannelName() {
        return this.channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
