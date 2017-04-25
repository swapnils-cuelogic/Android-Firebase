package com.cuelogic.firebase.chat.core.registration;

import android.app.Activity;

import com.cuelogic.firebase.chat.models.User;

public interface RegisterContract {
    interface View {
        void onRegistrationSuccess(User user);

        void onRegistrationFailure(String message);
    }

    interface Presenter {
        void register(Activity activity, String displayName, String email, String password);
    }

    interface Interactor {
        void performFirebaseRegistration(Activity activity, String displayName, String email, String password);
    }

    interface OnRegistrationListener {
        void onSuccess(User user);

        void onFailure(String message);
    }
}
