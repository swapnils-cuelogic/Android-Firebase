package com.cuelogic.firebase.chat.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersContract;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersPresenter;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.models.Room;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.ui.activities.ChatActivity;
import com.cuelogic.firebase.chat.ui.adapters.ChatsListingRecyclerAdapter;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.ItemClickSupport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecentChatsFragment extends BaseFragment implements GetUsersContract.View, ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private List<Room> rooms = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private ChatsListingRecyclerAdapter mChatsListingRecyclerAdapter;

    private GetUsersPresenter mGetUsersPresenter;
    private boolean isFirstTime;

    public static RecentChatsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        RecentChatsFragment fragment = new RecentChatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private BroadcastReceiver messageReceivedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshGroups();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstTime = true;
        getActivity().registerReceiver(messageReceivedReceiver, new IntentFilter(Constants.ACTION_MESSAGE_RECEIVED));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isFirstTime) {
            isFirstTime = false;
        } else {
            refreshGroups();
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

        mChatsListingRecyclerAdapter = new ChatsListingRecyclerAdapter(mContext, rooms);
        mRecyclerViewAllUserListing.setAdapter(mChatsListingRecyclerAdapter);

        refreshGroups();
        /*mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });*/

        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerViewAllUserListing)
                .setOnItemLongClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void refreshGroups() {
        rooms.clear();
        mGetUsersPresenter.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public void onRefresh() {
        refreshGroups();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ChatActivity.startActivity(getActivity(), mChatsListingRecyclerAdapter.getRoom(position), null);
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        onListItemSelect(position);
        return true;
    }

    private void onListItemSelect(final int position) {
        final Room room = mChatsListingRecyclerAdapter.getRoom(position);
        final boolean isMuted = ChatRoomsDBM.getInstance(mContext).isMuted(room.roomId);
        String menuText = getString(R.string.mute);
        if(isMuted) {
            menuText = getString(R.string.unmute);
        }
        final CharSequence[] items = {menuText};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(getString(R.string.select_action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(isMuted) {
                    ChatRoomsDBM.getInstance(mContext).unmuteRoom(room.roomId);
                } else {
                    ChatRoomsDBM.getInstance(mContext).muteRoom(room.roomId);
                }
                if (mChatsListingRecyclerAdapter != null) {
                    mChatsListingRecyclerAdapter.notifyItemChanged(position);
                }
            }
        });
        builder.show();
    }

    //List item select method
    /*private void onListItemSelect(int position) {
        mChatsListingRecyclerAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = mChatsListingRecyclerAdapter.getSelectedCount() > 0;//Check if any items are already selected or not

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new GroupsToolbarActionModeCallback(getActivity(), mChatsListingRecyclerAdapter, mChatsListingRecyclerAdapter.getGroups()));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(mChatsListingRecyclerAdapter
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
    }*/

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
            List<String> chatIds = new ArrayList<>();
            if(user.indRooms != null)
                chatIds.addAll(user.indRooms);
            if(user.grpRooms != null)
                chatIds.addAll(user.grpRooms);
            for (String groupId:
                    chatIds) {
                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(groupId).getRef().keepSynced(true);
                FirebaseDatabase.getInstance().getReference().child(Constants.ARG_ROOMS).child(groupId).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Room room = dataSnapshot.getValue(Room.class);
                        if(room != null) {
                            if(room.type == Constants.TYPE_GROUP) {
                                rooms.add(room);
                                Collections.sort(rooms);
                                mChatsListingRecyclerAdapter.notifyDataSetChanged();
                                FirebaseMessaging.getInstance().subscribeToTopic(room.roomId); //Not needed
                            } else {
                                String selfId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                for (String uid:
                                        room.users) {
                                    if(!selfId.equals(uid)) {
                                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uid).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                room.displayName = user.displayName;
                                                room.photoUrl = user.photoUrl;
                                                room.email = user.email;

                                                rooms.add(room);
                                                Collections.sort(rooms);
                                                mChatsListingRecyclerAdapter.notifyDataSetChanged();
                                                FirebaseMessaging.getInstance().subscribeToTopic(room.roomId); //Not needed
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
