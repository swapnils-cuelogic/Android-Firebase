package com.cuelogic.firebase.chat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.cuelogic.firebase.chat.BuildConfig;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersContract;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersPresenter;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.DashboardActivity;
import com.cuelogic.firebase.chat.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends BaseFragment implements GetUsersContract.View, View.OnClickListener {
    private Button mBtnLogout;
    private CircleImageView profileImage;
    private TextView textViewUserName, textViewEmailId, textViewAppVersion;
    private Switch switchNotificationAlert;
    private GetUsersPresenter mGetUsersPresenter;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        textViewUserName = (TextView) view.findViewById(R.id.text_view_user_name);
        textViewEmailId = (TextView) view.findViewById(R.id.text_view_email_id);
        switchNotificationAlert = (Switch) view.findViewById(R.id.switchNotificationAlert);
        textViewAppVersion = (TextView) view.findViewById(R.id.text_view_app_version);
        view.findViewById(R.id.text_view_privacy_policy).setOnClickListener(this);
        view.findViewById(R.id.text_view_terms_and_conditions).setOnClickListener(this);

        mBtnLogout = (Button) view.findViewById(R.id.button_logout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        switchNotificationAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefUtil.setNotificationsEnabled(mContext, isChecked);
            }
        });
        mBtnLogout.setOnClickListener(this);
        textViewAppVersion.setText(BuildConfig.VERSION_NAME);

        mGetUsersPresenter = new GetUsersPresenter(this);
        mGetUsersPresenter.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        switchNotificationAlert.setChecked(SharedPrefUtil.isNotificationsEnabled(mContext));
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.text_view_privacy_policy:
                showToastShort(getString(R.string.privacy_policy));
                break;
            case R.id.text_view_terms_and_conditions:
                showToastShort(getString(R.string.terms_and_conditions));
                break;
            case R.id.button_logout:
                onLogout(view);
            break;
        }
    }

    private void onLogout(View view) {
        ((DashboardActivity)getActivity()).logout();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onGetAllUsersFailure(String message) {

    }

    @Override
    public void onGetUserSuccess(User user) {
        if(user != null) {
            Picasso.with(mContext).load(user.photoUrl).into(profileImage);
            textViewUserName.setText(user.displayName);
            textViewEmailId.setText(user.email);
        }
    }

    @Override
    public void onGetUserFailure(String message) {

    }

    @Override
    public void onGetChatUsersSuccess(List<User> users) {

    }

    @Override
    public void onGetChatUsersFailure(String message) {

    }
}
