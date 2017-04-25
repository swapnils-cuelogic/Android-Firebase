package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.fragments.ChatFragment;
import com.cuelogic.firebase.chat.utils.Constants;

public class ChatActivity extends BaseActivity {
    private User user;

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        user = getIntent().getParcelableExtra(Constants.ARG_USER);
        if(user != null) {
            // set toolbar title
            mToolbar.setTitle(user.displayName != null ? user.displayName : user.email);

            if(user.email != null) {
                mToolbar.setSubtitle(user.email);
            }
            enableBackButton();
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
        FirebaseChatMainApp.setChatActivityOpen(true, user.uid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false, "");
    }
}
