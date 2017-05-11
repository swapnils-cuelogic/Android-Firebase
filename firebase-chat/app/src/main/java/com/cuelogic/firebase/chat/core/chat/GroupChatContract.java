package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;

import com.cuelogic.firebase.chat.models.NewChat;

import java.util.List;

public interface GroupChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(NewChat newChat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, NewChat newChat, List<String> receiverFirebaseTokens);

        void getMessage(String roomId);

        void syncMessage(String roomId);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, NewChat newChat, List<String> receiverFirebaseTokens);

        void getMessageFromFirebaseUser(String roomId);

        void syncMessageFromFirebaseUser(String roomId);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(NewChat newChat);

        void onGetMessagesFailure(String message);
    }
}
