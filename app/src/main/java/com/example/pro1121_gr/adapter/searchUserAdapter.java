package com.example.pro1121_gr.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.activity.ChatActivity;
import com.example.pro1121_gr.databinding.SearchUserBinding;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class searchUserAdapter extends FirestoreRecyclerAdapter<userModel, searchUserAdapter.UserModelViewHolder> {

    private Context context;
    public searchUserAdapter(@NonNull FirestoreRecyclerOptions<userModel> options, Application context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull userModel model) {
        // Kiểm tra nếu là tài khoản của người dùng hiện tại, không hiển thị
        if (model.getUserId().equals(firebaseUtil.currentUserId())) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }

        firebaseUtil.getCurrentOtherProfileImageStorageReference(model.getUserId()).getDownloadUrl().addOnCompleteListener(task ->{
            if (task.isSuccessful()){
                Uri uri = task.getResult();
                firebaseUtil.setAvatar(context, uri, holder.binding.itemAvatar);
            }
        });

        holder.binding.itemUsername.setText(model.getUsername());
        holder.binding.itemPhonenumber.setText(model.getPhone());

        // xu ly su kien click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                StaticFunction.passUserModelAsIntent(intent, model);
                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SearchUserBinding binding = SearchUserBinding.inflate(inflater, parent, false);

        return new UserModelViewHolder(binding);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{
        SearchUserBinding binding;
        public UserModelViewHolder(@NonNull SearchUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
