package com.cuelogic.firebase.chat.core.users.add;

import android.content.Context;

import com.cuelogic.firebase.chat.models.User;

public interface AddUserContract {
    interface View {
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, User user);
    }

    interface Interactor {
        void addUserToDatabase(Context context, User user);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
