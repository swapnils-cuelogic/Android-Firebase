package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public class Group implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeString(this.displayName);
        dest.writeLong(this.createdTime);
        dest.writeString(this.createdBy);
        dest.writeLong(this.lastUpdatedTime);
        dest.writeStringList(this.users);
    }

    protected Group(Parcel in) {
        this.roomId = in.readString();
        this.displayName = in.readString();
        this.createdTime = in.readLong();
        this.createdBy = in.readString();
        this.lastUpdatedTime = in.readLong();
        this.users = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
