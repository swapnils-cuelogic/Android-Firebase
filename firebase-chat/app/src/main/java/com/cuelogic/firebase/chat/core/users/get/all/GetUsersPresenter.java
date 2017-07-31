package com.cuelogic.firebase.chat.core.users.get.all;

import com.cuelogic.firebase.chat.models.User;

import java.util.List;

public class GetUsersPresenter implements GetUsersContract.Presenter, GetUsersContract.OnGetAllUsersListener, GetUsersContract.OnGetUserListener {
    private GetUsersContract.View mView;
    private GetUsersInteractor mGetUsersInteractor;

    public GetUsersPresenter(GetUsersContract.View view) {
        this.mView = view;
        mGetUsersInteractor = new GetUsersInteractor(this, this);
    }

    @Override
    public void getAllUsers() {
        mGetUsersInteractor.getAllUsersFromFirebase();
    }

    @Override
    public void getUser(String uid) {
        mGetUsersInteractor.getUserFromFirebase(uid);
    }

    @Override
    public void getChatUsers() {
        mGetUsersInteractor.getChatUsersFromFirebase();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        mView.onGetAllUsersSuccess(users);
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        mView.onGetAllUsersFailure(message);
    }

    @Override
    public void onGetUserSuccess(User user) {
        mView.onGetUserSuccess(user);
    }

    @Override
    public void onGetUserFailure(String message) {
        mView.onGetUserFailure(message);
    }
}
