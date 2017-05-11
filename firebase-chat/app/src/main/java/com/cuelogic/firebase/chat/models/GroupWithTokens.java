package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Harshal Vibhandik on 11/05/17.
 */

public class GroupWithTokens implements Parcelable {
    public Group group;
    public List<String> tokens;

    public GroupWithTokens() {
    }

    public GroupWithTokens(Group group, List<String> tokens) {
        this.group = group;
        this.tokens = tokens;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.group, 0);
        dest.writeStringList(this.tokens);
    }

    protected GroupWithTokens(Parcel in) {
        this.group = in.readParcelable(Group.class.getClassLoader());
        this.tokens = in.createStringArrayList();
    }

    public static final Parcelable.Creator<GroupWithTokens> CREATOR = new Parcelable.Creator<GroupWithTokens>() {
        public GroupWithTokens createFromParcel(Parcel source) {
            return new GroupWithTokens(source);
        }

        public GroupWithTokens[] newArray(int size) {
            return new GroupWithTokens[size];
        }
    };
}
