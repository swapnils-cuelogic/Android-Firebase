package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;

import com.cuelogic.firebase.chat.models.GroupChat;

public interface GroupChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(GroupChat newChat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, GroupChat newChat);

        void getMessage(String roomId);

        void syncMessage(String roomId);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, GroupChat newChat);

        void getMessageFromFirebaseUser(String roomId);

        void syncMessageFromFirebaseUser(String roomId);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(GroupChat newChat);

        void onGetMessagesFailure(String message);
    }
}
