package com.example.pro1121_gr.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.activity.ChatActivity;
import com.example.pro1121_gr.databinding.ItemListAvatarBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ChatListAvatarAdapter extends FirestoreRecyclerAdapter<chatRoomModel, ChatListAvatarAdapter.ChatListAvatarAdapterViewHolder> {

    private final Context context;

    public ChatListAvatarAdapter(@NonNull FirestoreRecyclerOptions<chatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatListAvatarAdapterViewHolder holder, int position, @NonNull chatRoomModel model) {
        UserDAO.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                userModel otherUserModel = task.getResult().toObject(userModel.class);
                int status = 0;
                if (otherUserModel != null) {
                    status = otherUserModel.getStatus();
                }
                UserDAO.getCurrentOtherProfileImageStorageReference(otherUserModel != null ? otherUserModel.getUserId() : null).getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Uri uri = task1.getResult();
                        UserDAO.setAvatar(context, uri, holder.listAvatarBinding.itemAvatar);
                    }
                });
                if (otherUserModel != null) {
                    holder.listAvatarBinding.fullName.setText(getLastName(otherUserModel.getUsername()));

                }
                if (status == 0) {
                    holder.listAvatarBinding.imgCircleOffline.setVisibility(View.VISIBLE);
                    holder.listAvatarBinding.imgCircleOnline.setVisibility(View.GONE);
                }else {
                    holder.listAvatarBinding.imgCircleOffline.setVisibility(View.GONE);
                    holder.listAvatarBinding.imgCircleOnline.setVisibility(View.VISIBLE);
                }

                holder.itemView.setOnClickListener(v -> {
                    //navigate to chat activity
                    Intent intent = new Intent(context, ChatActivity.class);
                    if (otherUserModel != null) {
                        Functions.passUserModelAsIntent(intent,otherUserModel);
                    } else Functions.Toasty(context, context.getString(R.string.error), Functions.error);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
            }
        });
    }

    @NonNull
    @Override
    public ChatListAvatarAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListAvatarBinding listAvatarBinding = ItemListAvatarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatListAvatarAdapterViewHolder(listAvatarBinding);
    }

    public static class ChatListAvatarAdapterViewHolder extends RecyclerView.ViewHolder{
        ItemListAvatarBinding listAvatarBinding;
        public ChatListAvatarAdapterViewHolder(@NonNull ItemListAvatarBinding listAvatarBinding) {
            super(listAvatarBinding.getRoot());
            this.listAvatarBinding = listAvatarBinding;
        }
    }

    private static String getLastName(String fullName) {
        // Kiểm tra xem chuỗi đầu vào có null hay không
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        // Tách tên thành mảng các từ
        String[] nameParts = fullName.trim().split("\\s+");

        // Lấy phần tử cuối cùng (tên)
        return nameParts[nameParts.length - 1];
    }

}
