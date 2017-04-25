package com.cuelogic.firebase.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User implements Parcelable {
    public String uid;
    public String email;
    public String firebaseToken;
    public String displayName;
    public String photoUrl;

    public User() {
    }

    public User(String uid, String email, String firebaseToken, String displayName) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.displayName = displayName;
    }

    public User(String uid, String email, String firebaseToken, String displayName, String photoUrl) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.email);
        dest.writeString(this.firebaseToken);
        dest.writeString(this.displayName);
        dest.writeString(this.photoUrl);
    }

    protected User(Parcel in) {
        this.uid = in.readString();
        this.email = in.readString();
        this.firebaseToken = in.readString();
        this.displayName = in.readString();
        this.photoUrl = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
