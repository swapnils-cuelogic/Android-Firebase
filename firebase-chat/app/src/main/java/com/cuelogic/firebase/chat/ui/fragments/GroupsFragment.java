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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersContract;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersPresenter;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.listeners.GroupsToolbarActionModeCallback;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.NewChatActivity;
import com.cuelogic.firebase.chat.ui.adapters.GroupListingRecyclerAdapter;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.ItemClickSupport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends BaseFragment implements GetUsersContract.View, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private List<Group> groups = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private GroupListingRecyclerAdapter mGroupListingRecyclerAdapter;

    private GetUsersPresenter mGetUsersPresenter;
    private ActionMode mActionMode;

    public static GroupsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        GroupsFragment fragment = new GroupsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver messageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mGroupListingRecyclerAdapter != null) {
                mGroupListingRecyclerAdapter.notifyDataSetChanged();
            }
        }
    };

    public GroupListingRecyclerAdapter getGroupListingRecyclerAdapter() {
        return mGroupListingRecyclerAdapter;
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(messageReceivedReceiver, new IntentFilter(Constants.ACTION_MESSAGE_RECEIVED));
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

        mGroupListingRecyclerAdapter = new GroupListingRecyclerAdapter(mContext, groups);
        mRecyclerViewAllUserListing.setAdapter(mGroupListingRecyclerAdapter);

        refreshGroups();
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

    private void refreshGroups() {
        groups.clear();
        mGetUsersPresenter.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public void onRefresh() {
        refreshGroups();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (mActionMode != null)
            onListItemSelect(position);
        else
            NewChatActivity.startActivity(getActivity(), mGroupListingRecyclerAdapter.getGroup(position));
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        onListItemSelect(position);
        return true;
    }

    //List item select method
    private void onListItemSelect(int position) {
        mGroupListingRecyclerAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mGroupListingRecyclerAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new GroupsToolbarActionModeCallback(getActivity(), mGroupListingRecyclerAdapter, mGroupListingRecyclerAdapter.getGroups()));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mGroupListingRecyclerAdapter
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

    public void muteNotifications(List<Group> selectedGroups) {
        List<String> roomIds = new ArrayList<>();
        for (Group group:
                selectedGroups) {
            roomIds.add(group.roomId);
        }
        ChatRoomsDBM.getInstance(mContext).muteRoom(roomIds);
        if (mGroupListingRecyclerAdapter != null) {
            mGroupListingRecyclerAdapter.notifyDataSetChanged();
        }
    }
    public void unmuteNotifications(List<Group> selectedGroups) {
        List<String> roomIds = new ArrayList<>();
        for (Group group:
                selectedGroups) {
            roomIds.add(group.roomId);
        }
        ChatRoomsDBM.getInstance(mContext).unmuteRoom(roomIds);
        if (mGroupListingRecyclerAdapter != null) {
            mGroupListingRecyclerAdapter.notifyDataSetChanged();
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
    }

    @Override
    public void onGetAllUsersFailure(String message) {
//        showAlertMessage("Error: " + message);
    }

    @Override
    public void onGetUserSuccess(User user) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        if(user != null) {
            List<String> groupIds = user.rooms;
            if(groupIds != null) {
                for (String groupId:
                     groupIds) {
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(groupId).getRef().keepSynced(true);
                    FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(groupId).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if(group != null) {
                                groups.add(group);
                                mGroupListingRecyclerAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        }
    }

    @Override
    public void onGetUserFailure(String message) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onGetChatUsersSuccess(List<User> users) {
    }

    @Override
    public void onGetChatUsersFailure(String message) {

    }
}
