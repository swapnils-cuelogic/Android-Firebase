package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;

import com.cuelogic.firebase.chat.models.RoomChat;

public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(RoomChat newChat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, RoomChat newChat, String title);

        void getMessage(String roomId);

        void syncMessage(String roomId);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, RoomChat newChat, String title);

        void getMessageFromFirebaseUser(String roomId);

        void syncMessageFromFirebaseUser(String roomId);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(RoomChat newChat);

        void onGetMessagesFailure(String message);
    }
}
