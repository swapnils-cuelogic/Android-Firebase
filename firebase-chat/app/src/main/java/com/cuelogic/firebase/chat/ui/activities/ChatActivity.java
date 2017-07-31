package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.Room;
import com.cuelogic.firebase.chat.ui.fragments.ChatFragment;
import com.cuelogic.firebase.chat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class ChatActivity extends BaseActivity {
    private Room room;

    private static final String TAG = ChatActivity.class.getSimpleName();

    public static void startActivity(Context context, Room room, String uid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_GROUP, room);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        room = getIntent().getParcelableExtra(Constants.ARG_GROUP);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (room != null) {
                enableBackButton();
                // set the register screen fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout_content_chat, ChatFragment.newInstance(room), ChatFragment.class.getSimpleName());
                fragmentTransaction.commit();
            } else {
                onBackPressed();
                showToastShort(getString(R.string.invalid_users_data));
            }
        } else {
            showToastShort(getString(R.string.invalid_user_session));
            GoogleSignInActivity.startIntent(ChatActivity.this,
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            DashboardActivity.startActivity(this);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true, room.roomId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false, "");
    }
}
