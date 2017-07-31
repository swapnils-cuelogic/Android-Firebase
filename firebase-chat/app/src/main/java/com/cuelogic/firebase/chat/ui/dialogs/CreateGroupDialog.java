package com.cuelogic.firebase.chat.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.listeners.GroupActionListener;
import com.cuelogic.firebase.chat.models.Room;
import com.cuelogic.firebase.chat.models.RoomWithTokens;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.adapters.UserListingRecyclerAdapter;
import com.cuelogic.firebase.chat.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshal Vibhandik on 09/05/17.
 */

public class CreateGroupDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private List<User> groupMembers;
    private EditText editTextGroupName;
    private RecyclerView rvGroupMembers;
    private GroupActionListener groupActionListener;

    public CreateGroupDialog(@NonNull Context context, List<User> users) {
        super(context);
        mContext = context;
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
        groupMembers.get(groupMembers.size()-1).displayName = "You";
        rvGroupMembers.setAdapter(new UserListingRecyclerAdapter(mContext, groupMembers));
        /*StringBuilder membersBuilder = new StringBuilder();
        int count = 0;
        for (User member:
             groupMembers) {
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(member.uid)) {
                membersBuilder.append(++count).append(". You");
            } else {
                membersBuilder.append(++count).append(". ").append(member.displayName).append("\n");
            }
        }
        textViewGroupMembers.setText(membersBuilder.toString());*/
    }

    private void initUiElements() {
        editTextGroupName = (EditText)findViewById(R.id.editTextGroupName);
        rvGroupMembers = (RecyclerView)findViewById(R.id.rvGroupMembers);

        findViewById(R.id.buttonCreate).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCreate :
                if(isValidGroupInfo()) {
                    RoomWithTokens roomWithTokens = createGropInfo();
                    groupActionListener.onCreateGroupRequest(roomWithTokens, null);
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
            editTextGroupName.setError("Please enter room name");
        } else {
            editTextGroupName.setError(null);
        }
        return editTextGroupName.getError() == null;
    }

    private RoomWithTokens createGropInfo() {
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
        return new RoomWithTokens(new Room(roomId, Constants.TYPE_GROUP, displayName, createdTime, createdBy, createdTime, users), tokens);
    }
}
