package com.cuelogic.firebase.chat.core.chat;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cuelogic.firebase.chat.fcm.FcmNotificationBuilder;
import com.cuelogic.firebase.chat.models.GroupChat;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class GroupChatInteractor implements GroupChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private GroupChatContract.OnSendMessageListener mOnSendMessageListener;
    private GroupChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public GroupChatInteractor() {
    }

    public GroupChatInteractor(GroupChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public GroupChatInteractor(GroupChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public GroupChatInteractor(GroupChatContract.OnSendMessageListener onSendMessageListener,
                               GroupChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final GroupChat newChat) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).keepSynced(true);
        String messageId = FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).push().getKey();
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(newChat.roomId).child(messageId).setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //Need to write this code to send notifications to all users
                    sendPushNotificationToReceivers("Group Message", newChat.senderUid, newChat.roomId, newChat.message, newChat.timestamp);
                    mOnSendMessageListener.onSendMessageSuccess();
                } else {
                    mOnSendMessageListener.onSendMessageFailure("Unable to send message: ");
                    //getMessageFromFirebaseUser(newChat.roomId);
                }
            }
        });
    }

    private void sendPushNotificationToReceivers(String title, String uid, String roomId, String message, long timestamp) {
        FcmNotificationBuilder.initialize()
                .type(2).title(title).message(message)
                .uid(uid).roomId(roomId).timeStamp(timestamp)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(String roomId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_MESSAGES).child(roomId).getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String string) {
                GroupChat newChat = dataSnapshot.getValue(GroupChat.class);
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
