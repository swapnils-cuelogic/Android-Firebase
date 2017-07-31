package com.cuelogic.firebase.chat.core.users.get.all;

import com.cuelogic.firebase.chat.models.User;

import java.util.List;

public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);

        void onGetUserSuccess(User user);

        void onGetUserFailure(String message);

        void onGetChatUsersSuccess(List<User> users);

        void onGetChatUsersFailure(String message);
    }

    interface Presenter {
        void getAllUsers();

        void getUser(String uid);

        void getChatUsers();
    }

    interface Interactor {
        void getAllUsersFromFirebase();

        void getUserFromFirebase(String uid);

        void getChatUsersFromFirebase();
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);
    }

    interface OnGetUserListener {
        void onGetUserSuccess(User user);

        void onGetUserFailure(String message);
    }

    interface OnGetChatUsersListener {
        void onGetChatUsersSuccess(List<User> users);

        void onGetChatUsersFailure(String message);
    }
}
