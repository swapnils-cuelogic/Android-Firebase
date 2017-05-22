package com.cuelogic.firebase.chat.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.database.ChatRoomsDBM;
import com.cuelogic.firebase.chat.models.Group;
import com.cuelogic.firebase.chat.models.RoomDetails;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.utils.Constants;
import com.cuelogic.firebase.chat.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListingRecyclerAdapter extends RecyclerView.Adapter<GroupListingRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;
    private SparseBooleanArray mSelectedItemsIds;

    public GroupListingRecyclerAdapter(Context context, List<Group> groups) {
        this.mContext = context;
        this.mGroups = groups;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void add(Group group) {
        mGroups.add(group);
        notifyItemInserted(mGroups.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_group_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Group group = mGroups.get(position);
        try {
            //String alphabet = user.email.substring(0, 1);
            /** Change background color of the selected items in list view  **/
            holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);

            final RoomDetails roomDetails = ChatRoomsDBM.getInstance(mContext).getRoomDetails(group.roomId);

            if(group.type == Constants.TYPE_GROUP) {
                holder.txtGroupName.setText(group.displayName);
                holder.groupImage.setImageResource(R.drawable.ic_group_white_24dp);
                holder.txtMembers.setText(StringUtils.isNotEmptyNotNull(roomDetails.lastMessage) ? roomDetails.lastMessage : group.users.size() + " Members");
            } else {
                String selfId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (String uid:
                        group.users) {
                    if(!selfId.equals(uid)) {
                        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uid).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                holder.txtGroupName.setText(user.displayName);
                                Picasso.with(mContext).load(user.photoUrl).into(holder.groupImage);
                                holder.txtMembers.setText(StringUtils.isNotEmptyNotNull(roomDetails.lastMessage) ? roomDetails.lastMessage : user.email);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        break;
                    }
                }
            }

            holder.txtLastMessageTime.setText(roomDetails.lastMessageTime);

            if(roomDetails.isMuted) {
                holder.imgIsMuted.setVisibility(View.VISIBLE);
            } else {
                holder.imgIsMuted.setVisibility(View.INVISIBLE);
            }

            if(roomDetails.unreadCount > 0) {
                holder.txtUnreadCount.setText(""+roomDetails.unreadCount);
                holder.txtUnreadCount.setVisibility(View.VISIBLE);
            } else {
                holder.txtUnreadCount.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mGroups != null) {
            return mGroups.size();
        }
        return 0;
    }

    public Group getGroup(int position) {
        return mGroups.get(position);
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtGroupName, txtMembers, txtLastMessageTime, txtUnreadCount;
        private CircleImageView groupImage;
        private ImageView imgIsMuted;

        ViewHolder(View itemView) {
            super(itemView);
            groupImage = (CircleImageView) itemView.findViewById(R.id.group_image);
            txtGroupName = (TextView) itemView.findViewById(R.id.text_view_group_name);
            txtMembers = (TextView) itemView.findViewById(R.id.text_view_members);
            imgIsMuted = (ImageView) itemView.findViewById(R.id.image_is_muted);
            txtLastMessageTime = (TextView) itemView.findViewById(R.id.text_view_last_message_time);
            txtUnreadCount = (TextView) itemView.findViewById(R.id.text_view_unread_count);
        }
    }


    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }


    //Remove selected selections
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    //Get total selected count
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    //Return all selected ids
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
