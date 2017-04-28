package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.logout.LogoutContract;
import com.cuelogic.firebase.chat.core.logout.LogoutPresenter;
import com.cuelogic.firebase.chat.ui.adapters.UserListingPagerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class UserListingActivity extends BaseActivity implements LogoutContract.View {
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;

    private LogoutPresenter mLogoutPresenter;
    private GoogleApiClient mGoogleApiClient;

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
        mToolbar.setTitle(getString(R.string.users));

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
        UserListingPagerAdapter userListingPagerAdapter = new UserListingPagerAdapter(getSupportFragmentManager());
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);

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
}
