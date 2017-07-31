package com.cuelogic.firebase.chat.listeners;

import com.cuelogic.firebase.chat.models.RoomWithTokens;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public interface GroupActionListener {
    void onCreateGroupRequest(RoomWithTokens roomWithTokens, Callback callback);
}
