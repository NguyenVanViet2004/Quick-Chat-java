package com.example.pro1121_gr.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class chatAdapter extends FirestoreRecyclerAdapter<chatMesseageModel, chatAdapter.ChatModelViewHolder> {

    private Context context;
    private String uriOther;
    private static String TAG = chatAdapter.ChatModelViewHolder.class.toString();

    public chatAdapter(@NonNull FirestoreRecyclerOptions<chatMesseageModel> options, Context context, String uriOther) {
        super(options);
        this.context = context;
        this.uriOther = uriOther;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull chatMesseageModel model) {

        if(model.getSenderId().equals(firebaseUtil.currentUserId())){

            firebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri uri = null;
                    if (task.isSuccessful()) {
                        uri = task.getResult();
                        firebaseUtil.setAvatar(context, uri, holder.myAVT);
                    } else {
                        Log.e(TAG, "Download URL not successful");
                    }
                }
            });


            if (StaticFunction.isURL(model.getMessage())){
                holder.mySendImg.setVisibility(View.VISIBLE);
                holder.rightChatTextview.setVisibility(View.GONE);
                holder.myAVT.setVisibility(View.GONE);
                // Sử dụng Glide để hiển thị ảnh từ URL vào ImageView
                firebaseUtil.loadImageInChat(context, model.getMessage(), holder.mySendImg);
            }else{
                holder.rightChatTextview.setText(model.getMessage());
                holder.mySendImg.setVisibility(View.GONE);
                holder.myAVT.setVisibility(View.VISIBLE);
            }
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.otherAVT.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
        }else{ // xử lý giao diện chat của đối phương

            firebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri uri = null;
                    if (task.isSuccessful()) {
                        uri = task.getResult();
                        firebaseUtil.setAvatar(context, Uri.parse(uriOther), holder.otherAVT);
                    } else {
                        Log.e(TAG, "Download URL not successful");
                    }
                }
            });

            if (StaticFunction.isURL(model.getMessage())){
                holder.otherSendImg.setVisibility(View.VISIBLE);
                holder.leftChatTextview.setVisibility(View.GONE);
                holder.otherAVT.setVisibility(View.GONE);
                // Sử dụng Glide để hiển thị ảnh từ URL vào ImageView
                firebaseUtil.loadImageInChat(context, model.getMessage(), holder.otherSendImg);
            }else {
                holder.leftChatTextview.setText(model.getMessage());
                holder.otherSendImg.setVisibility(View.GONE);
                holder.otherAVT.setVisibility(View.VISIBLE);
            }
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.myAVT.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);

        }

    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_layout,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;

        ImageView otherAVT, myAVT, mySendImg, otherSendImg;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            otherAVT = itemView.findViewById(R.id.item_avatar);
            myAVT = itemView.findViewById(R.id.item_avatar2);
            mySendImg = itemView.findViewById(R.id.mySendImg);
            otherSendImg = itemView.findViewById(R.id.otherSendImg);
        }
    }
}
