package com.cuelogic.firebase.chat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    public UserListingRecyclerAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.mUsers = users;
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
            if(StringUtils.isNotEmptyNotNull(user.photoUrl)) {
                Picasso.with(mContext).load(user.photoUrl).into(holder.profileImage);
            } else {
                //TODO code set alphabet
            }
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUsername;
        private CircleImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            txtUsername = (TextView) itemView.findViewById(R.id.text_view_username);
        }
    }
}
