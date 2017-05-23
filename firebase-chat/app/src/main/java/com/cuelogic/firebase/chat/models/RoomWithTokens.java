package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 11/05/17.
 */

public class RoomWithTokens implements Parcelable {
    public Room room;
    public List<String> tokens;

    public RoomWithTokens() {
    }

    public RoomWithTokens(Room room, List<String> tokens) {
        this.room = room;
        this.tokens = tokens;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.room, 0);
        dest.writeStringList(this.tokens);
    }

    protected RoomWithTokens(Parcel in) {
        this.room = in.readParcelable(Room.class.getClassLoader());
        this.tokens = in.createStringArrayList();
    }

    public static final Parcelable.Creator<RoomWithTokens> CREATOR = new Parcelable.Creator<RoomWithTokens>() {
        public RoomWithTokens createFromParcel(Parcel source) {
            return new RoomWithTokens(source);
        }

        public RoomWithTokens[] newArray(int size) {
            return new RoomWithTokens[size];
        }
    };
}
