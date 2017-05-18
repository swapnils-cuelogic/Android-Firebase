package com.cuelogic.firebase.chat.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersContract;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersPresenter;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.listeners.UsersToolbarActionModeCallback;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.ChatActivity;
import com.cuelogic.firebase.chat.ui.adapters.UserListingRecyclerAdapter;
import com.cuelogic.firebase.chat.ui.dialogs.CreateGroupDialog;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.ItemClickSupport;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends BaseFragment implements GetUsersContract.View, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private UserListingRecyclerAdapter mUserListingRecyclerAdapter;

    private GetUsersPresenter mGetUsersPresenter;
    private ActionMode mActionMode;

    private List<User> selectedUsers;

    public UserListingRecyclerAdapter getUserListingRecyclerAdapter() {
        return mUserListingRecyclerAdapter;
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public static UsersFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver messageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mUserListingRecyclerAdapter != null) {
                mUserListingRecyclerAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(messageReceivedReceiver, new IntentFilter(Constants.ACTION_MESSAGE_RECEIVED));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUserListingRecyclerAdapter != null) {
            mUserListingRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(messageReceivedReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_users, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerViewAllUserListing = (RecyclerView) view.findViewById(R.id.recycler_view_all_user_listing);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mGetUsersPresenter = new GetUsersPresenter(this);
        getUsers();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemLongClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getUsers();
    }

    private void getUsers() {
        if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_CHATS)) {

        } else if (TextUtils.equals(getArguments().getString(ARG_TYPE), TYPE_ALL)) {
            mGetUsersPresenter.getAllUsers();
        }
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mUserListingRecyclerAdapter = new UserListingRecyclerAdapter(mContext, users);
        mRecyclerViewAllUserListing.setAdapter(mUserListingRecyclerAdapter);
        mUserListingRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetAllUsersFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        showAlertMessage("Error: " + message);
    }

    @Override
    public void onGetUserSuccess(User user) {
        selectedUsers.add(user);
        new CreateGroupDialog(getActivity(), selectedUsers).show();
    }

    @Override
    public void onGetUserFailure(String message) {
        showToastShort("Unable to create group");
    }

    @Override
    public void onGetChatUsersSuccess(List<User> users) {

    }

    @Override
    public void onGetChatUsersFailure(String message) {

    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (mActionMode != null)
            onListItemSelect(position);
        else
            ChatActivity.startActivity(getActivity(), mUserListingRecyclerAdapter.getUser(position));
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        onListItemSelect(position);
        return true;
    }

    //List item select method
    private void onListItemSelect(int position) {
        mUserListingRecyclerAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mUserListingRecyclerAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new UsersToolbarActionModeCallback(getActivity(), mUserListingRecyclerAdapter, mUserListingRecyclerAdapter.getUsers()));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mUserListingRecyclerAdapter
                    .getSelectedCount()) + " selected");
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    //Set action mode null after use
    public void createGroupOfUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
        mGetUsersPresenter.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
    public void muteNotifications(List<User> selectedUsers) {
        List<String> roomIds = new ArrayList<>();
        for (User user:
                selectedUsers) {
            roomIds.add(user.uid);
        }
        ChatRoomsDBM.getInstance(mContext).muteRoom(roomIds);
        if (mUserListingRecyclerAdapter != null) {
            mUserListingRecyclerAdapter.notifyDataSetChanged();
        }
    }
    public void unmuteNotifications(List<User> selectedUsers) {
        List<String> roomIds = new ArrayList<>();
        for (User user:
                selectedUsers) {
            roomIds.add(user.uid);
        }
        ChatRoomsDBM.getInstance(mContext).unmuteRoom(roomIds);
        if (mUserListingRecyclerAdapter != null) {
            mUserListingRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
