package com.example.pro1121_gr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.activity.ChatActivity;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class chatListAdapter extends FirestoreRecyclerAdapter<chatRoomModel,chatListAdapter.chatListAdapterViewHolder> {

    private Context context;

    public chatListAdapter(@NonNull FirestoreRecyclerOptions<chatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull chatListAdapterViewHolder holder, int position, @NonNull chatRoomModel model) {
        firebaseUtil.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                boolean lastMessageSendByMe = model.getLastMessageSenderId().equals(firebaseUtil.currentUserId());

                userModel otherUserModel = task.getResult().toObject(userModel.class);

                firebaseUtil.getCurrentOtherProfileImageStorageReference(otherUserModel.getUserId()).getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        firebaseUtil.setAvatar(context,uri,holder.avatar);
                    }
                });
                holder.usernameText.setText(otherUserModel.getUsername());

                if (lastMessageSendByMe) holder.lastMessageText.setText("Bạn : " + model.getLastMessage());
                else holder.lastMessageText.setText(model.getLastMessage());

                holder.lastMessageTime.setText(firebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                holder.itemView.setOnClickListener(v -> {
                    //navigate to chat activity
                    Intent intent = new Intent(context, ChatActivity.class);
                    StaticFunction.passUserModelAsIntent(intent,otherUserModel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
            }
        });
    }

    @NonNull
    @Override
    public chatListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false);
        return new chatListAdapterViewHolder(view);
    }

    class chatListAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView avatar;

        public chatListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.item_username);
            lastMessageText = itemView.findViewById(R.id.last_chat);
            lastMessageTime = itemView.findViewById(R.id.timeSend);
            avatar = itemView.findViewById(R.id.item_avatar);
        }
    }
}
