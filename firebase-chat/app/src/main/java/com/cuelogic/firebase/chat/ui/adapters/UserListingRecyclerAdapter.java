package com.cuelogic.firebase.chat.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.utils.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListingRecyclerAdapter extends RecyclerView.Adapter<UserListingRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;
    private SparseBooleanArray mSelectedItemsIds;

    public UserListingRecyclerAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.mUsers = users;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void add(User user) {
        mUsers.add(user);
        notifyItemInserted(mUsers.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_user_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUsers.get(position);
        try {
            //String alphabet = user.email.substring(0, 1);
            holder.txtUsername.setText(user.displayName != null ? user.displayName : user.email);
            holder.txtEmail.setText(user.email);
            if(StringUtils.isNotEmptyNotNull(user.photoUrl)) {
                Picasso.with(mContext).load(user.photoUrl).into(holder.profileImage);
            } else {
                //TODO code set alphabet
            }
            /** Change background color of the selected items in list view  **/
            holder.itemView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);

            /*RoomDetails roomDetails = ChatRoomsDBM.getInstance(mContext).getRoomDetails(user.uid);

            holder.txtEmail.setText(StringUtils.isNotEmptyNotNull(roomDetails.lastMessage) ? roomDetails.lastMessage : user.email);
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
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mUsers != null) {
            return mUsers.size();
        }
        return 0;
    }

    public User getUser(int position) {
        return mUsers.get(position);
    }

    public List<User> getUsers() {
        return mUsers;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername, txtEmail;
        private CircleImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            txtUsername = (TextView) itemView.findViewById(R.id.text_view_username);
            txtEmail = (TextView) itemView.findViewById(R.id.text_view_email);
            /*imgIsMuted = (ImageView) itemView.findViewById(R.id.image_is_muted);
            txtLastMessageTime = (TextView) itemView.findViewById(R.id.text_view_last_message_time);
            txtUnreadCount = (TextView) itemView.findViewById(R.id.text_view_unread_count);*/
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
