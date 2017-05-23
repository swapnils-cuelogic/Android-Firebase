package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;

import com.cuelogic.firebase.chat.models.RoomChat;

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ChatContract.OnGetMessagesListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this);
    }

    @Override
    public void sendMessage(Context context, RoomChat newChat, String title) {
        mChatInteractor.sendMessageToFirebaseUser(context, newChat, title);
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
    public void onGetMessagesSuccess(RoomChat newChat) {
        mView.onGetMessagesSuccess(newChat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }
}
