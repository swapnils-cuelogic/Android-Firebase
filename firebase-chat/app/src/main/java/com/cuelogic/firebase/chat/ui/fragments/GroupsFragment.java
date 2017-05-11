package com.cuelogic.firebase.chat.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersContract;
import com.cuelogic.firebase.chat.core.users.get.all.GetUsersPresenter;
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

public class GroupsFragment extends BaseFragment implements GetUsersContract.View, ItemClickSupport.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";

    private List<Group> groups = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerViewAllUserListing;

    private GroupListingRecyclerAdapter mGroupListingRecyclerAdapter;

    private GetUsersPresenter mGetUsersPresenter;

    public static GroupsFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        GroupsFragment fragment = new GroupsFragment();
        fragment.setArguments(args);
        return fragment;
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
        NewChatActivity.startActivity(getActivity(), mGroupListingRecyclerAdapter.getGroup(position));
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
