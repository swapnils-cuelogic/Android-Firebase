package com.cuelogic.firebase.chat.utils;

public interface Constants {

    String ARG_USERS = "users";
    String ARG_ROOMS = "rooms";
    String ARG_USER = "display_name";
    String ARG_GROUP = "chat_group";
    String ARG_CHAT_ROOMS = "chat_rooms";
    String ARG_FIREBASE_TOKEN = "firebaseToken";
    String ARG_MESSAGES = "messages";
    boolean IS_APP_DEBUG = true;

    String ARG_IND_ROOMS = "indRooms";
    String ARG_GRP_ROOMS = "grpRooms";
    String APP_NAME = "FirebaseChatApp";

    int TYPE_INDIVIDUAL = 0;
    int TYPE_GROUP = 1;

    String ACTION_MESSAGE_RECEIVED = "com.cuelogic.firebase.chat.action_message_received";
}
