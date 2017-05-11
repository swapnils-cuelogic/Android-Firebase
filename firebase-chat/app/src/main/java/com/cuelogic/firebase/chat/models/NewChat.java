package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class NewChat implements Parcelable {
    public String roomId;
    public String senderUid;
    public String message;
    public long timestamp;

    public NewChat() {
    }

    public NewChat(String roomId, String senderUid, String message, long timestamp) {
        this.roomId = roomId;
        this.senderUid = senderUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeString(this.senderUid);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
    }

    protected NewChat(Parcel in) {
        this.roomId = in.readString();
        this.senderUid = in.readString();
        this.message = in.readString();
        this.timestamp = in.readLong();
    }

    public static final Creator<NewChat> CREATOR = new Creator<NewChat>() {
        public NewChat createFromParcel(Parcel source) {
            return new NewChat(source);
        }

        public NewChat[] newArray(int size) {
            return new NewChat[size];
        }
    };
}
