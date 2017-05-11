package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;

import com.cuelogic.firebase.chat.models.NewChat;

import java.util.List;

public class GroupChatPresenter implements GroupChatContract.Presenter, GroupChatContract.OnSendMessageListener,
        GroupChatContract.OnGetMessagesListener {
    private GroupChatContract.View mView;
    private GroupChatInteractor mChatInteractor;

    public GroupChatPresenter(GroupChatContract.View view) {
        this.mView = view;
        mChatInteractor = new GroupChatInteractor(this, this);
    }

    @Override
    public void sendMessage(Context context, NewChat newChat, List<String> receiverFirebaseTokens) {
        mChatInteractor.sendMessageToFirebaseUser(context, newChat, receiverFirebaseTokens);
    }

    @Override
    public void getMessage(String roomId) {
        mChatInteractor.getMessageFromFirebaseUser(roomId);
    }

    @Override
    public void syncMessage(String roomId) {
        mChatInteractor.syncMessageFromFirebaseUser(roomId);
    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(NewChat newChat) {
        mView.onGetMessagesSuccess(newChat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }
}
