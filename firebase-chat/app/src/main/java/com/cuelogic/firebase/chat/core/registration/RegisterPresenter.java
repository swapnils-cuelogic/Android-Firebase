package com.cuelogic.firebase.chat.core.registration;

import android.app.Activity;

import com.cuelogic.firebase.chat.models.User;

public class RegisterPresenter implements RegisterContract.Presenter, RegisterContract.OnRegistrationListener {
    private RegisterContract.View mRegisterView;
    private RegisterInteractor mRegisterInteractor;

    public RegisterPresenter(RegisterContract.View registerView) {
        this.mRegisterView = registerView;
        mRegisterInteractor = new RegisterInteractor(this);
    }

    @Override
    public void register(Activity activity, String displayName, String email, String password) {
        mRegisterInteractor.performFirebaseRegistration(activity, displayName, email, password);
    }

    @Override
    public void onSuccess(User user) {
        mRegisterView.onRegistrationSuccess(user);
    }

    @Override
    public void onFailure(String message) {
        mRegisterView.onRegistrationFailure(message);
    }
}
