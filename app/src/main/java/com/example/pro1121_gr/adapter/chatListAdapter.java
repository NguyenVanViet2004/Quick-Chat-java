package com.example.pro1121_gr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.activity.ChatActivity;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

public class chatListAdapter extends FirestoreRecyclerAdapter<chatRoomModel,chatListAdapter.chatListAdapterViewHolder> {

    private final Context context;

    public chatListAdapter(@NonNull FirestoreRecyclerOptions<chatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint({"SetTextI18n", "LogNotTimber"})
    @Override
    protected void onBindViewHolder(@NonNull chatListAdapterViewHolder holder, int position, @NonNull chatRoomModel model) {
        UserDAO.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                boolean lastMessageSendByMe = model.getLastMessageSenderId().equals(UserDAO.currentUserId());

                userModel otherUserModel = task.getResult().toObject(userModel.class);

                try {
                    UserDAO.getCurrentOtherProfileImageStorageReference(otherUserModel != null ? otherUserModel.getUserId() : null).getDownloadUrl().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Uri uri = task1.getResult();
                            UserDAO.setAvatar(context,uri,holder.avatar);
                        }
                    });
                    if (otherUserModel != null) {
                        holder.usernameText.setText(otherUserModel.getUsername());
                        if (otherUserModel.getStatus() == 0) {
                            holder.img_Circle_offline.setVisibility(View.VISIBLE);
                            holder.img_Circle_online.setVisibility(View.GONE);
                        } else {
                            holder.img_Circle_offline.setVisibility(View.GONE);
                            holder.img_Circle_online.setVisibility(View.VISIBLE);
                        }
                    }

                    if (lastMessageSendByMe) holder.lastMessageText.setText("Bạn : " + model.getLastMessage());
                    else holder.lastMessageText.setText(model.getLastMessage());
                    if (Functions.isURL(model.getLastMessage().trim())) holder.lastMessageText.setText("[Image]");

                    holder.lastMessageTime.setText(Functions.timestampToString(model.getLastMessageTimestamp()));

                    holder.itemView.setOnClickListener(v -> {
                        //navigate to chat activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        if (otherUserModel != null) {
                            Functions.passUserModelAsIntent(intent,otherUserModel);
                        }else Functions.Toasty(context, context.getString(R.string.error), Functions.error);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    });
                }catch (Exception e){
                    Log.e(chatListAdapter.class.getSimpleName(), Objects.requireNonNull(e.getMessage()));
                }
            }
        });
    }

    @NonNull
    @Override
    public chatListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);
        return new chatListAdapterViewHolder(view);
    }

    static class chatListAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView avatar, img_Circle_online, img_Circle_offline;

        public chatListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.item_username);
            lastMessageText = itemView.findViewById(R.id.last_chat);
            lastMessageTime = itemView.findViewById(R.id.timeSend);
            avatar = itemView.findViewById(R.id.item_avatar);
            img_Circle_online = itemView.findViewById(R.id.img_Circle_online);
            img_Circle_offline = itemView.findViewById(R.id.img_Circle_offline);
        }
    }
}
