package com.cuelogic.firebase.chat.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.listeners.GroupActionListener;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.GroupWithTokens;
import com.cuelogic.firebase.chat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshal Vibhandik on 09/05/17.
 */

public class CreateGroupDialog extends Dialog implements View.OnClickListener {
    private List<User> groupMembers;
    private EditText editTextGroupName;
    private TextView textViewGroupMembers;
    private GroupActionListener groupActionListener;

    public CreateGroupDialog(@NonNull Context context, List<User> users) {
        super(context);
        groupMembers = users;
        groupActionListener = (GroupActionListener)context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_create_group);

        initUiElements();

        setGroupMembersInfo();
    }

    private void setGroupMembersInfo() {
        StringBuilder membersBuilder = new StringBuilder();
        int count = 0;
        for (User member:
             groupMembers) {
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(member.uid)) {
                membersBuilder.append(++count).append(". You");
            } else {
                membersBuilder.append(++count).append(". ").append(member.displayName).append("\n");
            }
        }
        textViewGroupMembers.setText(membersBuilder.toString());
    }

    private void initUiElements() {
        editTextGroupName = (EditText)findViewById(R.id.editTextGroupName);
        textViewGroupMembers = (TextView)findViewById(R.id.textViewGroupMembers);

        findViewById(R.id.buttonCreate).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreate :
                if(isValidGroupInfo()) {
                    GroupWithTokens groupWithTokens = createGropInfo();
                    groupActionListener.onCreateGroupRequest(groupWithTokens);
                    dismiss();
                }
                break;

            case R.id.buttonCancel :
                dismiss();
                break;
        }
    }

    private boolean isValidGroupInfo() {
        if("".equals(editTextGroupName.getText().toString().trim())) {
            editTextGroupName.setError("Please enter group name");
        } else {
            editTextGroupName.setError(null);
        }
        return editTextGroupName.getError() == null;
    }

    private GroupWithTokens createGropInfo() {
        String createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        long createdTime = System.currentTimeMillis();
        String roomId = FirebaseDatabase.getInstance().getReference().push().getKey();
        String displayName = editTextGroupName.getText().toString().trim();
        List<String> users = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        for (User member:
             groupMembers) {
            users.add(member.uid);
            tokens.add(member.firebaseToken);
        }
        return new GroupWithTokens(new Group(roomId, displayName, createdTime, createdBy, createdTime, users), tokens);
    }
}
