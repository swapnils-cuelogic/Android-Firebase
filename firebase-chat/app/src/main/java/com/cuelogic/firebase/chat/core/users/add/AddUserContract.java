package com.cuelogic.firebase.chat.core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

public interface AddUserContract {
    interface View {
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser, String displayName);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser, String displayName);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
