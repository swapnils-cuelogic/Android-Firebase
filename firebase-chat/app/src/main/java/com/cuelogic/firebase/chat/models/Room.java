package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public class Room implements Parcelable, Comparable<Room> {
    public String roomId;
    public int type;
    public String displayName;
    public String photoUrl;
    public String email;
    public long createdTime;
    public String createdBy;
    public Long lastUpdatedTime;
    public List<String> users;

    public Room() {
    }

    public Room(String roomId, int type, String displayName, long createdTime, String createdBy, long lastUpdatedTime, List<String> users) {
        this.roomId = roomId;
        this.type = type;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeInt(this.type);
        dest.writeString(this.displayName);
        dest.writeString(this.photoUrl);
        dest.writeString(this.email);
        dest.writeLong(this.createdTime);
        dest.writeString(this.createdBy);
        dest.writeLong(this.lastUpdatedTime);
        dest.writeStringList(this.users);
    }

    protected Room(Parcel in) {
        this.roomId = in.readString();
        this.type = in.readInt();
        this.displayName = in.readString();
        this.photoUrl = in.readString();
        this.email = in.readString();
        this.createdTime = in.readLong();
        this.createdBy = in.readString();
        this.lastUpdatedTime = in.readLong();
        this.users = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int compareTo(@NonNull Room room) {
        return room.lastUpdatedTime.compareTo(this.lastUpdatedTime);
    }
}
