package com.cuelogic.firebase.chat.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.ui.fragments.GroupChatFragment;
import com.cuelogic.firebase.chat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class GroupChatActivity extends BaseActivity {
    private Group group;

    private static final String TAG = GroupChatActivity.class.getSimpleName();

    public static void startActivity(Context context, Group group) {
        Intent intent = new Intent(context, GroupChatActivity.class);
        intent.putExtra(Constants.ARG_GROUP, group);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat);
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        group = getIntent().getParcelableExtra(Constants.ARG_GROUP);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (group != null) {
                // set toolbar title
                mToolbar.setTitle(group.displayName);

                mToolbar.setSubtitle(group.users.size()+" Members");
                enableBackButton();
                // set the register screen fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout_content_chat, GroupChatFragment.newInstance(group), GroupChatFragment.class.getSimpleName());
                fragmentTransaction.commit();
            } else {
                onBackPressed();
                showToastShort(getString(R.string.invalid_users_data));
            }
        } else {
            showToastShort(getString(R.string.invalid_user_session));
            GoogleSignInActivity.startIntent(GroupChatActivity.this,
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            UserListingActivity.startActivity(this);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true, group.roomId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false, "");
    }
}
