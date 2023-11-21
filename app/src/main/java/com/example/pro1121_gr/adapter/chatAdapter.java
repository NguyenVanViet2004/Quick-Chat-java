package com.example.pro1121_gr.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
import com.example.pro1121_gr.custom_textview.utils;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.CustomTypefaceInfo;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import es.dmoral.toasty.Toasty;

public class chatAdapter extends FirestoreRecyclerAdapter<chatMesseageModel, chatAdapter.ChatModelViewHolder> {

    private Context context;
    private String uriOther, chatRoomID;
    private static String TAG = chatAdapter.ChatModelViewHolder.class.toString();


    public chatAdapter(@NonNull FirestoreRecyclerOptions<chatMesseageModel> options, Context context, String uriOther, String ChatRoomId) {
        super(options);
        this.context = context;
        this.uriOther = uriOther;
        this.chatRoomID = ChatRoomId;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull chatMesseageModel model) {
        String documentId = getSnapshots().getSnapshot(position).getId();
        if (model.getSenderId().equals(firebaseUtil.currentUserId()))
            setChatLeftLayout(holder, model, documentId);
            // xử lý giao diện chat của đối phương
        else setChatRightLayout(holder, model);
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

    private void setChatLeftLayout(ChatModelViewHolder holder, chatMesseageModel model, String documentId) {
        firebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(task -> {
            Uri uri = null;
            if (task.isSuccessful()) {
                uri = task.getResult();
                firebaseUtil.setAvatar(context, uri, holder.myAVT);
            } else {
                Log.e(TAG, "Download URL not successful");
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
        if (model.getTypeface() != null) {
            utils.setFontForTextView(holder.rightChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        } else model.setTypeface(new CustomTypefaceInfo("RobotoLightTextView"));
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.otherAVT.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);

        holder.rightChatTextview.setOnLongClickListener(view -> {
            firebaseUtil.getChatRoomReference(chatRoomID)
                    .collection("chats")
                    .document(documentId)
                    .update("message", "Tin nhắn đã bị thu hồi!").addOnSuccessListener(aVoid -> {
                        Toasty.success(context, "Thu hồi tin nhắn thành công!", Toasty.LENGTH_LONG, true).show();
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "onLongClick: " + e.getMessage());
                        Toasty.error(context, "Thu hồi tin nhắn thất bại!", Toasty.LENGTH_LONG, true).show();
                    });
            return false;
        });
    }

    private void setChatRightLayout(ChatModelViewHolder holder, chatMesseageModel model) {
        firebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(task -> {
            Uri uri = null;
            if (task.isSuccessful()) {
                uri = task.getResult();
                firebaseUtil.setAvatar(context, Uri.parse(uriOther), holder.otherAVT);
            } else {
                Log.e(TAG, "Download URL not successful");
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
        if (model.getTypeface() != null){
            utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        } else model.setTypeface(new CustomTypefaceInfo("RobotoLightTextView"));
        //utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.myAVT.setVisibility(View.GONE);
        holder.leftChatLayout.setVisibility(View.VISIBLE);

    }


    private Typeface getTypeface(String type){
        if (type.equals("RobotoBoldTextView")) return utils.getRobotoBoldTypeFace(context);
        else if (type.equals("RobotoItalicTextView")) return utils.getRobotoItalicTypeFace(context);
        else if (type.equals("BlackjackTextview")) return utils.getBlackjackTypeFace(context);
        else if (type.equals("AlluraTextView")) return utils.getAlluraTypeFace(context);
        else return utils.getRobotoLightTypeFace(context);
    }

}
