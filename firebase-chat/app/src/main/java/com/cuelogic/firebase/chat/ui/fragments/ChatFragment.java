package com.cuelogic.firebase.chat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.chat.ChatContract;
import com.cuelogic.firebase.chat.core.chat.ChatPresenter;
import com.cuelogic.firebase.chat.database.ChatTableHelper;
import com.cuelogic.firebase.chat.events.PushNotificationEvent;
import com.cuelogic.firebase.chat.models.Chat;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.adapters.ChatRecyclerAdapter;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

public class ChatFragment extends Fragment implements ChatContract.View, TextView.OnEditorActionListener {
    private User user;
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    public static ChatFragment newInstance(User user) {
        Logger.vLog(TAG, "newInstance()", true);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }


    private void init() {
        user = getArguments().getParcelable(Constants.ARG_USER);
        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.uid);

        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverUid = user.uid; //usergetArguments().getString(Constants.ARG_RECEIVER_UID);
        if (null != receiverUid) {
            List<Chat> mRecords = ChatTableHelper.getRecords(senderUid, receiverUid);
            Logger.vLog(TAG, "Database Chat Records: " + mRecords.size());
            if (mChatRecyclerAdapter == null) {
                mChatRecyclerAdapter = new ChatRecyclerAdapter(mRecords);
                mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
                if (mChatRecyclerAdapter.getItemCount() > 0)
                    mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
            }
        }
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
        String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Chat chat = new Chat(sender, user.email, senderUid, user.uid, message, System.currentTimeMillis(), displayName);
        mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, user.firebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(FirebaseChatMainApp.getAppContext(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(FirebaseChatMainApp.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(final Chat chat) {
        Logger.vLog(TAG, "onGetMessagesSuccess()", true);
        //        if (mChatRecyclerAdapter == null) {
        //            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
        //            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        //        }
        //mChats.add(chat);
        mChatRecyclerAdapter.add(chat);
        //mChatRecyclerAdapter.notifyDataSetChanged();
        //mChatRecyclerAdapter.notifyDataSetChanged();
//        if (mChatRecyclerAdapter.getItemCount() > 0)
//            mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Logger.vLog(TAG, "onGetMessagesFailure()", true);
        Toast.makeText(FirebaseChatMainApp.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        Logger.vLog(TAG, "onPushNotificationEvent()", true);
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
//        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                pushNotificationEvent.getUid());
    }
}
