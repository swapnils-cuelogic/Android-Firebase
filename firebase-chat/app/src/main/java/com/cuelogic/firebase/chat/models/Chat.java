package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Chat implements Parcelable {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;
    public String displayName;
    public long id;

    public Chat() {
    }

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp, String displayName) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
        this.displayName = displayName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sender);
        dest.writeString(this.receiver);
        dest.writeString(this.senderUid);
        dest.writeString(this.receiverUid);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.displayName);
    }

    protected Chat(Parcel in) {
        this.sender = in.readString();
        this.receiver = in.readString();
        this.senderUid = in.readString();
        this.receiverUid = in.readString();
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.displayName = in.readString();
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        public Chat createFromParcel(Parcel source) {
            return new Chat(source);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };
}
