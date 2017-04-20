package com.cuelogic.firebase.chat.database.model;

import com.cuelogic.firebase.chat.models.Chat;

/**
 * @version 1.0.0
 * @Date 20/04/2017
 */

public class ChatInfoMapper {

    public static ChatInfo getDbObject(Chat chat) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setMessage(chat.message);
        chatInfo.setReceiver(chat.receiver);
        chatInfo.setReceiverUid(chat.receiverUid);
        chatInfo.setSender(chat.sender);
        chatInfo.setSenderUid(chat.senderUid);
        chatInfo.setTimestamp(chat.timestamp);
        return chatInfo;
    }

    public static Chat getBean(ChatInfo chatInfo) {
        Chat chat = new Chat();
        chat.message = chatInfo.getMessage();
        chat.sender = chatInfo.getSender();
        chat.senderUid = chatInfo.getSenderUid();
        chat.receiver = chatInfo.getReceiver();
        chat.receiverUid = chatInfo.getReceiverUid();
        chat.timestamp = chatInfo.getTimestamp();
        return chat;
    }

}
