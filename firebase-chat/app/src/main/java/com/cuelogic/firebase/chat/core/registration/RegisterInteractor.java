package com.cuelogic.firebase.chat.core.registration;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterInteractor implements RegisterContract.Interactor {
    private static final String TAG = RegisterInteractor.class.getSimpleName();

    private RegisterContract.OnRegistrationListener mOnRegistrationListener;

    public RegisterInteractor(RegisterContract.OnRegistrationListener onRegistrationListener) {
        this.mOnRegistrationListener = onRegistrationListener;
    }

    @Override
    public void performFirebaseRegistration(Activity activity, final String displayName, String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Add the user to users table.
                                                /*DatabaseReference database= FirebaseDatabase.getInstance().getReference();
                                                User user = new User(task.getResult().getUser().getUid(), email);
                                                database.child("users").push().setValue(user);*/
                                                mOnRegistrationListener.onSuccess(user);
                                            } else {
                                                mOnRegistrationListener.onFailure(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            mOnRegistrationListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }
}
