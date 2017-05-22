package com.cuelogic.firebase.chat.listeners;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.ui.activities.UserListingActivity;
import com.cuelogic.firebase.chat.ui.adapters.GroupListingRecyclerAdapter;
import com.cuelogic.firebase.chat.ui.fragments.GroupsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshal Vibhandik on 05/05/17.
 */

public class GroupsToolbarActionModeCallback implements ActionMode.Callback {
    private Context mContext;
    private GroupListingRecyclerAdapter groupListingRecyclerAdapter;
    private List<Group> groupsList;


    public GroupsToolbarActionModeCallback(Context context, GroupListingRecyclerAdapter groupListingRecyclerAdapter, List<Group> groupsList) {
        this.mContext = context;
        this.groupListingRecyclerAdapter = groupListingRecyclerAdapter;
        this.groupsList = groupsList;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_groups_action, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        //Get selected ids on basis of current fragment action mode
        SparseBooleanArray selected = groupListingRecyclerAdapter.getSelectedIds();

        int selectedMessageSize = selected.size();

        List<Group> selectedGroups = new ArrayList<>();
        //Loop to all selected items
        for (int i = (selectedMessageSize - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //get selected data in Model
                Group group = groupsList.get(selected.keyAt(i));
                selectedGroups.add(group);
            }
        }
        mode.finish();//Finish action mode

        Fragment recyclerFragment = ((UserListingActivity)mContext).getFragment(0);//Get recycler view fragment

        switch (item.getItemId()) {
            case R.id.action_mute_notifications:
                if (recyclerFragment != null)
                    ((GroupsFragment) recyclerFragment).muteNotifications(selectedGroups);
                break;
            case R.id.action_unmute_notifications:
                if (recyclerFragment != null)
                    ((GroupsFragment) recyclerFragment).unmuteNotifications(selectedGroups);
                break;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        groupListingRecyclerAdapter.removeSelection();  // remove selection
        Fragment recyclerFragment = ((UserListingActivity)mContext).getFragment(0);//Get recycler fragment
        if (recyclerFragment != null)
            ((GroupsFragment) recyclerFragment).setNullToActionMode();//Set action mode null
    }
}
