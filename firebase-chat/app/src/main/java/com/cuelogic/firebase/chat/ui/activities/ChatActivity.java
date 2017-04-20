package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.fragments.ChatFragment;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.Logger;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private User user;

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Logger.vLog(TAG, "onCreate()", true);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);
        user = getIntent().getParcelableExtra(Constants.ARG_USER);
        if(user != null) {
            // set toolbar title
            mToolbar.setTitle(user.displayName != null ? user.displayName : user.email);
            // set the register screen fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_content_chat, ChatFragment.newInstance(user), ChatFragment.class.getSimpleName());
            fragmentTransaction.commit();
        } else {
            onBackPressed();
            Toast.makeText(this, "Invalid users data found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true, getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false, "");
    }
}
