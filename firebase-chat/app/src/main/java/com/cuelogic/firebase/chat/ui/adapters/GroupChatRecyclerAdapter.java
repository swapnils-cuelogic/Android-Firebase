package com.cuelogic.firebase.chat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuelogic.firebase.chat.R;
import com.cuelogic.firebase.chat.models.GroupChat;
import com.cuelogic.firebase.chat.models.User;
import com.cuelogic.firebase.chat.utils.StringUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;
    private Map<String, User> mapUidUser = new HashMap<>();
    private List<GroupChat> mChats;
    private static String TIME_FORMAT = "d/M/yyyy h:mm a";
    private SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);

    public GroupChatRecyclerAdapter(Context context, List<GroupChat> chats, Map<String, User> mapUidUser) {
        this.mContext = context;
        this.mChats = chats;
        this.mapUidUser = mapUidUser;
    }

    public void add(GroupChat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        GroupChat chat = mChats.get(position);
        try {
            myChatViewHolder.txtTimestamp.setText(sdfTime.format(new Date(chat.timestamp)));
        }catch (Exception e) {
            e.printStackTrace();
        }

        User user = mapUidUser.get(chat.senderUid);
        if(user != null && StringUtils.isNotEmptyNotNull(user.photoUrl)) {
            Picasso.with(mContext).load(user.photoUrl).into(myChatViewHolder.imgUserPhoto);
        } else {
            myChatViewHolder.imgUserPhoto.setImageResource(R.drawable.ic_user_white_24dp);
        }
        myChatViewHolder.txtChatMessage.setText(chat.message);
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        GroupChat chat = mChats.get(position);
        try {
            otherChatViewHolder.txtTimestamp.setText(sdfTime.format(new Date(chat.timestamp)));
        }catch (Exception e) {
            e.printStackTrace();
        }

        User user = mapUidUser.get(chat.senderUid);
        if(user != null && StringUtils.isNotEmptyNotNull(user.photoUrl)) {
            Picasso.with(mContext).load(user.photoUrl).into(otherChatViewHolder.imgUserPhoto);
        } else {
            otherChatViewHolder.imgUserPhoto.setImageResource(R.drawable.ic_user_white_24dp);
        }
        if(user != null && StringUtils.isNotEmptyNotNull(user.displayName)) {
            otherChatViewHolder.txtUserName.setText(user.displayName);
            otherChatViewHolder.txtUserName.setVisibility(View.VISIBLE);
        } else {
            otherChatViewHolder.txtUserName.setVisibility(View.GONE);
        }
        otherChatViewHolder.txtChatMessage.setText(chat.message);
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgUserPhoto;
        private TextView txtChatMessage, txtTimestamp;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtTimestamp = (TextView) itemView.findViewById(R.id.text_view_timestamp);
            imgUserPhoto = (CircleImageView) itemView.findViewById(R.id.img_user_photo);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgUserPhoto;
        private TextView txtChatMessage, txtUserName, txtTimestamp;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserName = (TextView) itemView.findViewById(R.id.text_view_user_name);
            txtTimestamp = (TextView) itemView.findViewById(R.id.text_view_timestamp);
            imgUserPhoto = (CircleImageView) itemView.findViewById(R.id.img_user_photo);
        }
    }
}
