package com.cuelogic.firebase.chat.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cuelogic.firebase.chat.FirebaseChatMainApp;
import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.chat.ChatInteractor;
import com.cuelogic.firebase.chat.core.chat.GroupChatInteractor;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.events.PushNotificationEvent;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.ChatActivity;
import com.cuelogic.firebase.chat.ui.activities.GroupChatActivity;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.SharedPrefUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                int type = Integer.parseInt(remoteMessage.getData().get("type"));
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("text");
                String uid = remoteMessage.getData().get("uid");
                String roomId = remoteMessage.getData().get("room_id");
                long timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));
                String username = remoteMessage.getData().get("username");
                String fcmToken = remoteMessage.getData().get("fcm_token");

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if(type == 2) {
                    if(firebaseUser != null && !uid.equals(firebaseUser.getUid())) {
                        ChatRoomsDBM.getInstance(this).addMessage(roomId, message, timestamp);
                        sendBroadcast(new Intent(Constants.ACTION_MESSAGE_RECEIVED));
                        new GroupChatInteractor().syncMessageFromFirebaseUser(roomId);
                        // Don't show notification if chat activity is open.
                        if (!FirebaseChatMainApp.isRoomsOpen() && !FirebaseChatMainApp.isChattingWithSameUser(roomId) && !ChatRoomsDBM.getInstance(this).isMuted(roomId) && SharedPrefUtil.isNotificationsEnabled(this)) {
                            sendGroupNotification(message, roomId);
                        } else {
                            EventBus.getDefault().post(new PushNotificationEvent(title, message, username, roomId, fcmToken));
                        }
                    }
                } else {
                    //To get sync chat with real time database on push notification received
                    if(firebaseUser != null && !uid.equals(firebaseUser.getUid())) {
                        ChatRoomsDBM.getInstance(this).addMessage(uid, message, timestamp);
                        sendBroadcast(new Intent(Constants.ACTION_MESSAGE_RECEIVED));
                        new ChatInteractor().syncMessageFromFirebaseUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid);
                        // Don't show notification if chat activity is open.
                        if (!FirebaseChatMainApp.isRoomsOpen() && !FirebaseChatMainApp.isChattingWithSameUser(uid) && !ChatRoomsDBM.getInstance(this).isMuted(uid) && SharedPrefUtil.isNotificationsEnabled(this)) {
                            sendNotification(title, message, username, uid, fcmToken);
                        } else {
                            EventBus.getDefault().post(new PushNotificationEvent(title, message, username, uid, fcmToken));
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title, String message, String receiver, String receiverUid, String firebaseToken) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ARG_USER, new User(receiverUid, receiver, firebaseToken, title));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_messaging)
                .setContentTitle(title!=null ? title : receiver)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(receiverUid, 0, notificationBuilder.build());
    }
    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendGroupNotification(final String message, String roomId) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(roomId).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                if(group != null) {
                    Intent intent = new Intent(MyFirebaseMessagingService.this, GroupChatActivity.class);
                    intent.putExtra(Constants.ARG_GROUP, group);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this)
                            .setSmallIcon(R.drawable.ic_messaging)
                            .setContentTitle(group.displayName)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(group.roomId, 0, notificationBuilder.build());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}