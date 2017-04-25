package com.cuelogic.firebase.chat.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.registration.RegisterContract;
import com.cuelogic.firebase.chat.core.registration.RegisterPresenter;
import com.cuelogic.firebase.chat.core.users.add.AddUserContract;
import com.cuelogic.firebase.chat.core.users.add.AddUserPresenter;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.UserListingActivity;
import com.cuelogic.firebase.chat.utils.StringUtils;

public class RegisterFragment extends BaseFragment implements View.OnClickListener, RegisterContract.View, AddUserContract.View {
    private static final String TAG = RegisterFragment.class.getSimpleName();

    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;

    private EditText mETxtName, mETxtEmail, mETxtPassword;
    private Button mBtnRegister;

    private ProgressDialog mProgressDialog;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mETxtName = (EditText) view.findViewById(R.id.edit_text_name);
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_register:
                onRegister(view);
                break;
        }
    }

    private void onRegister(View view) {
        if(isValidInfo()) {
            String name = mETxtName.getText().toString().trim();
            String emailId = mETxtEmail.getText().toString().trim();
            String password = mETxtPassword.getText().toString().trim();

            mRegisterPresenter.register(getActivity(), name, emailId, password);
            mProgressDialog.show();
        } else {
            showAlertMessage("Invalid data to register!");
        }
    }

    private boolean isValidInfo() {
        if("".equals(mETxtName.getText().toString().trim())) {
            mETxtName.setError("Invalid Name");
        } else {
            mETxtName.setError(null);
        }
        if("".equals(mETxtEmail.getText().toString().trim())) {
            mETxtEmail.setError("Invalid Email");
        } else {
            if(StringUtils.isValidEmail(mETxtEmail.getText().toString().trim())) {
                mETxtEmail.setError(null);
            } else {
                mETxtEmail.setError("Invalid Email");
            }
        }
        if("".equals(mETxtPassword.getText().toString().trim())) {
            mETxtPassword.setError("Invalid Password");
        } else {
            mETxtPassword.setError(null);
        }
        return mETxtName.getError() == null && mETxtEmail.getError() == null && mETxtPassword.getError() == null;
    }

    @Override
    public void onRegistrationSuccess(User user) {
        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        showToastShort("Registration Successful!");
        mAddUserPresenter.addUser(getActivity().getApplicationContext(), user);
    }

    @Override
    public void onRegistrationFailure(String message) {
        mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));
        Log.e(TAG, "onRegistrationFailure: " + message);
        showAlertMessage("Registration failed!\n" + message);
    }

    @Override
    public void onAddUserSuccess(String message) {
        mProgressDialog.dismiss();
        showToastShort(message);
        UserListingActivity.startActivity(getActivity(),
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onAddUserFailure(String message) {
        mProgressDialog.dismiss();
        showAlertMessage(message);
    }
}
