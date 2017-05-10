package com.cuelogic.firebase.chat.listeners;

import com.cuelogic.firebase.chat.models.Group;

/**
 * Created by Harshal Vibhandik on 10/05/17.
 */

public interface GroupActionListener {
    void onCreateGroupRequest(Group group);
}
