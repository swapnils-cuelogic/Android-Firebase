package com.cuelogic.firebase.chat.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.chat.ChatContract;
import com.cuelogic.firebase.chat.core.chat.ChatPresenter;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.events.PushNotificationEvent;
import com.cuelogic.firebase.chat.models.Chat;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.adapters.ChatRecyclerAdapter;
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
import java.util.Map;

public class ChatFragment extends BaseFragment implements ChatContract.View, View.OnClickListener {
    private User user;
    private Map<String, User> mapUidUser = new HashMap<>();
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;
    private ImageView imgSendMessage;

    public static ChatFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_USER, user);
        ChatFragment fragment = new ChatFragment();
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
        imgSendMessage = (ImageView) view.findViewById(R.id.imgSendMessage);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        user = getArguments().getParcelable(Constants.ARG_USER);
        putChatUsersInMap();

        ChatRoomsDBM.getInstance(mContext).clearCount(user.uid);
        getActivity().sendBroadcast(new Intent(Constants.ACTION_MESSAGE_RECEIVED));

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        //mProgressDialog.show();

        imgSendMessage.setOnClickListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.syncMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.uid);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.uid);
    }

    private void putChatUsersInMap() {
        mapUidUser.put(user.uid, user);
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    mapUidUser.put(user.uid, user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        if (StringUtils.isNotEmptyNotNull(message.trim())) {
            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Chat chat = new Chat(sender, user.email, senderUid, user.uid, message, System.currentTimeMillis(), displayName);
            mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, user.firebaseToken);
            ChatRoomsDBM.getInstance(mContext).updateLastMessage(chat.receiverUid, chat.message, chat.timestamp);
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
    public void onGetMessagesSuccess(Chat chat) {
        mProgressDialog.dismiss();
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(mContext, new ArrayList<Chat>(), mapUidUser);
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.scrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
        //mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgSendMessage:
                sendMessage();
                break;
        }
    }
}
