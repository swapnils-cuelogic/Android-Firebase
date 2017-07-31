package com.cuelogic.firebase.chat.listeners;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.DashboardActivity;
import com.cuelogic.firebase.chat.ui.adapters.UserListingRecyclerAdapter;
import com.cuelogic.firebase.chat.ui.fragments.UsersFragment;
import com.cuelogic.firebase.chat.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harshal Vibhandik on 05/05/17.
 */

public class UsersToolbarActionModeCallback implements ActionMode.Callback {
    private Context mContext;
    private UserListingRecyclerAdapter userListingRecyclerAdapter;
    private List<User> usersList;


    public UsersToolbarActionModeCallback(Context context, UserListingRecyclerAdapter recyclerView_adapter, List<User> usersList) {
        this.mContext = context;
        this.userListingRecyclerAdapter = recyclerView_adapter;
        this.usersList = usersList;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_users_action_selection, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_done_create_group), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        } else {
            menu.findItem(R.id.action_done_create_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        //Get selected ids on basis of current fragment action mode
        SparseBooleanArray selected = userListingRecyclerAdapter
                .getSelectedIds();

        int selectedMessageSize = selected.size();

        List<User> selectedUsers = new ArrayList<>();
        //Loop to all selected items
        for (int i = (selectedMessageSize - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //get selected data in Model
                User user = usersList.get(selected.keyAt(i));
                //Print the data to show if its working properly or not
                Logger.eLog("Selected Items", "Name - " + user.displayName);
                selectedUsers.add(user);
            }
        }
        mode.finish();//Finish action mode

        Fragment recyclerFragment = ((DashboardActivity)mContext).getFragment(1);//Get recycler view fragment

        switch (item.getItemId()) {
            case R.id.action_done_create_group:
                if (recyclerFragment != null)
                    //If recycler fragment not null
                    ((UsersFragment) recyclerFragment).createGroupOfUsers(selectedUsers);
                break;
            /*case R.id.action_mute_notifications:
                if (recyclerFragment != null)
                    ((UsersFragment) recyclerFragment).muteNotifications(selectedUsers);
                break;
            case R.id.action_unmute_notifications:
                if (recyclerFragment != null)
                    ((UsersFragment) recyclerFragment).unmuteNotifications(selectedUsers);
                break;*/
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        userListingRecyclerAdapter.removeSelection();  // remove selection
        Fragment recyclerFragment = ((DashboardActivity)mContext).getFragment(1);//Get recycler fragment
        if (recyclerFragment != null)
            ((UsersFragment) recyclerFragment).setNullToActionMode();//Set action mode null
    }
}
