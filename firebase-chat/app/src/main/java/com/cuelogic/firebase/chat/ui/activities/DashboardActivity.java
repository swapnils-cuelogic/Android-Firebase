package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.logout.LogoutContract;
import com.cuelogic.firebase.chat.core.logout.LogoutPresenter;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.fcm.FcmTopicBuilder;
import com.cuelogic.firebase.chat.listeners.Callback;
import com.cuelogic.firebase.chat.listeners.GroupActionListener;
import com.cuelogic.firebase.chat.models.Room;
import com.cuelogic.firebase.chat.models.RoomWithTokens;
import com.cuelogic.firebase.chat.ui.adapters.DashboardPagerAdapter;
import com.cuelogic.firebase.chat.ui.fragments.UsersFragment;
import com.cuelogic.firebase.chat.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity implements LogoutContract.View, GroupActionListener {
    private TabLayout mTabLayoutDashboard;
    private ViewPager mViewPagerDashboard;

    private LogoutPresenter mLogoutPresenter;
    private GoogleApiClient mGoogleApiClient;
    private DashboardPagerAdapter dashboardPagerAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_dashboard);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        //mToolbar.startActionMode(mActionModeCallback);
        //mToolbar.setTitle(getString(R.string.users));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mTabLayoutDashboard = (TabLayout) findViewById(R.id.tab_layout_dashboard);
        mViewPagerDashboard = (ViewPager) findViewById(R.id.view_pager_dashboard);
        mViewPagerDashboard.setOffscreenPageLimit(2);

        // set the view pager adapter
        dashboardPagerAdapter = new DashboardPagerAdapter(getSupportFragmentManager());
        mViewPagerDashboard.setAdapter(dashboardPagerAdapter);
        mViewPagerDashboard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Fragment recyclerFragment1 = getFragment(1);//Get recycler fragment
                if (recyclerFragment1 != null) {
                    if(((UsersFragment) recyclerFragment1).getActionMode() != null)
                        ((UsersFragment) recyclerFragment1).getActionMode().finish();
                }
            }
        });

        // attach tab layout with view pager
        mTabLayoutDashboard.setupWithViewPager(mViewPagerDashboard);

        mLogoutPresenter = new LogoutPresenter(this);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ChatRoomsDBM.getInstance(DashboardActivity.this).clear();
                        mLogoutPresenter.logout();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onLogoutSuccess(String message) {
        showToastShort(message);
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        GoogleSignInActivity.startIntent(DashboardActivity.this,
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                });
    }

    @Override
    public void onLogoutFailure(String message) {
        showAlertMessage(message);
    }

    //Return current fragment on basis of Position
    public Fragment getFragment(int pos) {
        return dashboardPagerAdapter.getItem(pos);
    }

    @Override
    public void onCreateGroupRequest(RoomWithTokens roomWithTokens, final Callback callback) {
        showProgress();
        final Room room = roomWithTokens.room;
        final List<String> tokens = roomWithTokens.tokens;
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_ROOMS).child(room.roomId).keepSynced(true);
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_ROOMS).child(room.roomId).setValue(room)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    final String groupChild = room.type == Constants.TYPE_INDIVIDUAL ? Constants.ARG_IND_ROOMS : Constants.ARG_GRP_ROOMS;
                    FirebaseDatabase.getInstance().getReference()
                            .child(Constants.ARG_USERS).keepSynced(true);
                    for (final String userId:
                         room.users) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(Constants.ARG_USERS).child(userId).child(groupChild)
                                .getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> existingRooms = (List<String>)dataSnapshot.getValue();
                                if(existingRooms == null)
                                    existingRooms = new ArrayList<>();
                                if(!existingRooms.contains(room.roomId))
                                    existingRooms.add(room.roomId);
                                FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS).child(userId).child(groupChild).setValue(existingRooms);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    String topicName = "/topics/"+ room.roomId;
                    FcmTopicBuilder.initialize()
                            .topicName(topicName)
                            .tokens(tokens)
                            .send();

                    if(callback != null) {
                        callback.onSuccess();
                    }
                    hideProgress();
                } else {
                    showToastShort(getString(R.string.error_group_creation));
                    if(callback != null) {
                        callback.onFailure(getString(R.string.error_group_creation));
                    }
                    hideProgress();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setRoomsActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setRoomsActivityOpen(false);
    }
}
