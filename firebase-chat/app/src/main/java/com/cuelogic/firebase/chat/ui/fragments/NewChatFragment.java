package com.cuelogic.firebase.chat.ui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.chat.GroupChatContract;
import com.cuelogic.firebase.chat.core.chat.GroupChatPresenter;
import com.cuelogic.firebase.chat.events.PushNotificationEvent;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.NewChat;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.adapters.GroupChatRecyclerAdapter;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewChatFragment extends BaseFragment implements GroupChatContract.View, TextView.OnEditorActionListener {
    private Group group;
    private Map<String, User> mapUidUser = new HashMap<>();
    private List<String> pushTokens = new ArrayList<>();

    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private GroupChatRecyclerAdapter mGroupChatRecyclerAdapter;

    private GroupChatPresenter mChatPresenter;

    public static NewChatFragment newInstance(Group group) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_GROUP, group);
        NewChatFragment fragment = new NewChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        group = getArguments().getParcelable(Constants.ARG_GROUP);
        setUpUsersMapAndPushTokens();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        //mProgressDialog.show();

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new GroupChatPresenter(this);
        mChatPresenter.syncMessage(group.roomId);
        mChatPresenter.getMessage(group.roomId);
    }

    private void setUpUsersMapAndPushTokens() {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                for (String uid:
                     group.users) {
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uid).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if(user != null) {
                                mapUidUser.put(user.uid, user);
                                if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(user.uid)) {
                                    pushTokens.add(user.firebaseToken);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        if(StringUtils.isNotEmptyNotNull(message.trim())) {
            /*String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();*/
            String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            NewChat newChat = new NewChat(group.roomId, senderUid, message, System.currentTimeMillis());
            mChatPresenter.sendMessage(getActivity().getApplicationContext(), newChat, pushTokens);
        }
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        //showToastShort(getString(R.string.message_sent));
    }

    @Override
    public void onSendMessageFailure(String message) {
        showAlertMessage(message);
    }

    @Override
    public void onGetMessagesSuccess(NewChat newChat) {
        mProgressDialog.dismiss();
        if (mGroupChatRecyclerAdapter == null) {
            mGroupChatRecyclerAdapter = new GroupChatRecyclerAdapter(new ArrayList<NewChat>(), mapUidUser);
            mRecyclerViewChat.setAdapter(mGroupChatRecyclerAdapter);
        }
        mGroupChatRecyclerAdapter.add(newChat);
        mRecyclerViewChat.scrollToPosition(mGroupChatRecyclerAdapter.getItemCount() - 1);
        //mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mGroupChatRecyclerAdapter == null || mGroupChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(group.roomId);
        }
    }
}
