package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public class Group implements Parcelable, Comparable<Group> {
    public String roomId;
    public int type;
    public String displayName;
    public String photoUrl;
    public long createdTime;
    public String createdBy;
    public Long lastUpdatedTime;
    public List<String> users;

    public Group() {
    }

    public Group(String roomId, int type, String displayName, long createdTime, String createdBy, long lastUpdatedTime, List<String> users) {
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
        dest.writeLong(this.createdTime);
        dest.writeString(this.createdBy);
        dest.writeLong(this.lastUpdatedTime);
        dest.writeStringList(this.users);
    }

    protected Group(Parcel in) {
        this.roomId = in.readString();
        this.type = in.readInt();
        this.displayName = in.readString();
        this.photoUrl = in.readString();
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

    @Override
    public int compareTo(@NonNull Group group) {
        return group.lastUpdatedTime.compareTo(this.lastUpdatedTime);
    }
}
