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
import android.view.Menu;
import android.view.MenuItem;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.logout.LogoutContract;
import com.cuelogic.firebase.chat.core.logout.LogoutPresenter;
import com.cuelogic.firebase.chat.fcm.FcmTopicBuilder;
import com.cuelogic.firebase.chat.listeners.GroupActionListener;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.GroupWithTokens;
import com.cuelogic.firebase.chat.ui.adapters.UserListingPagerAdapter;
import com.cuelogic.firebase.chat.ui.fragments.GroupsFragment;
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

public class UserListingActivity extends BaseActivity implements LogoutContract.View, GroupActionListener {
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;

    private LogoutPresenter mLogoutPresenter;
    private GoogleApiClient mGoogleApiClient;
    private UserListingPagerAdapter userListingPagerAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserListingActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, UserListingActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_listing);
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

        mTabLayoutUserListing = (TabLayout) findViewById(R.id.tab_layout_user_listing);
        mViewPagerUserListing = (ViewPager) findViewById(R.id.view_pager_user_listing);

        // set the view pager adapter
        userListingPagerAdapter = new UserListingPagerAdapter(getSupportFragmentManager());
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);
        mViewPagerUserListing.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Fragment recyclerFragment0 = getFragment(0);//Get recycler fragment
                if (recyclerFragment0 != null) {
                    if(((UsersFragment) recyclerFragment0).getActionMode() != null)
                        ((UsersFragment) recyclerFragment0).getActionMode().finish();
                }
                Fragment recyclerFragment1 = getFragment(1);//Get recycler fragment
                if (recyclerFragment1 != null) {
                    if(((GroupsFragment) recyclerFragment1).getActionMode() != null)
                        ((GroupsFragment) recyclerFragment1).getActionMode().finish();
                }
            }
        });

        // attach tab layout with view pager
        mTabLayoutUserListing.setupWithViewPager(mViewPagerUserListing);

        mLogoutPresenter = new LogoutPresenter(this);
    }

    @Override
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
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
                        GoogleSignInActivity.startIntent(UserListingActivity.this,
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
        return userListingPagerAdapter.getItem(pos);
    }

    @Override
    public void onCreateGroupRequest(GroupWithTokens groupWithTokens) {
        showProgress();
        final Group group = groupWithTokens.group;
        final List<String> tokens = groupWithTokens.tokens;
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_ROOMS).child(group.roomId).keepSynced(true);
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.ARG_ROOMS).child(group.roomId).setValue(group)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference()
                            .child(Constants.ARG_USERS).keepSynced(true);
                    for (final String userId:
                         group.users) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(Constants.ARG_USERS).child(userId).child(Constants.ARG_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<String> existingRooms = (List<String>)dataSnapshot.getValue();
                                if(existingRooms == null)
                                    existingRooms = new ArrayList<>();
                                existingRooms.add(group.roomId);
                                FirebaseDatabase.getInstance().getReference()
                                        .child(Constants.ARG_USERS).child(userId).child(Constants.ARG_ROOMS).setValue(existingRooms);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    String topicName = "/topics/"+group.roomId;
                    FcmTopicBuilder.initialize()
                            .topicName(topicName)
                            .tokens(tokens)
                            .send();

                    hideProgress();
                } else {
                    showToastShort(getString(R.string.error_group_creation));
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
