package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cuelogic.firebase.chat.fcm.FcmNotificationBuilder;
import com.cuelogic.firebase.chat.models.RoomChat;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor() {
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final RoomChat newChat, final String title) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).keepSynced(true);

        String messageId = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).push().getKey();

        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).child(messageId).setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {

                    sendPushNotificationToReceivers(title, newChat.senderUid, newChat.roomId, newChat.message, newChat.timestamp);

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("lastUpdatedTime", newChat.timestamp);
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(newChat.roomId).keepSynced(true);
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS)
                            .child(newChat.roomId)
                            .updateChildren(result);
                    mOnSendMessageListener.onSendMessageSuccess();
                } else {
                    mOnSendMessageListener.onSendMessageFailure("Unable to send message: ");
                }
            }
        });
    }

    private void sendPushNotificationToReceivers(String title, String uid, String roomId, String message, long timestamp) {
        FcmNotificationBuilder.initialize()
                .title(title).message(message)
                .uid(uid).roomId(roomId).timeStamp(timestamp)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(String roomId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(roomId).getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String string) {
                RoomChat newChat = dataSnapshot.getValue(RoomChat.class);
                mOnGetMessagesListener.onGetMessagesSuccess(newChat);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                Logger.vLog(TAG, "getMessageFromFirebaseUser: onCancelled", true);
            }
        });
    }

    @Override
    public void syncMessageFromFirebaseUser(String roomId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES)
                .child(roomId).getRef().keepSynced(true);
    }
}
