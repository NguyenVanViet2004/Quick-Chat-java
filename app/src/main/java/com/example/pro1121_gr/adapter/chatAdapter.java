package com.example.pro1121_gr.adapter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.custom_textview.utils;
import com.example.pro1121_gr.databinding.BottomOptionDialogBinding;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.CustomTypefaceInfo;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import es.dmoral.toasty.Toasty;

public class chatAdapter extends FirestoreRecyclerAdapter<chatMesseageModel, chatAdapter.ChatModelViewHolder> {

    private final Context context;
    private final String uriOther;
    private final String chatRoomID;
    private static final String TAG = chatAdapter.ChatModelViewHolder.class.toString();


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
            setChatRightLayout(holder, model, documentId);
            // xử lý giao diện chat của đối phương
        else setChatLeftLayout(holder, model);
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_layout,parent,false);
        return new ChatModelViewHolder(view);
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder{

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

    private void setChatRightLayout(ChatModelViewHolder holder, chatMesseageModel model, String documentId) {
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
            showBottomDialog(holder, documentId, model);
            return true;
        });
    }

    private void setChatLeftLayout(ChatModelViewHolder holder, chatMesseageModel model) {
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
        if (model.getTypeface() != null) {
            utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        } else model.setTypeface(new CustomTypefaceInfo("RobotoLightTextView"));
        //utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.myAVT.setVisibility(View.GONE);
        holder.leftChatLayout.setVisibility(View.VISIBLE);

        holder.leftChatTextview.setOnLongClickListener(view -> {
            copyToClipboard(model);
            return true;
        });

    }


    private void showBottomDialog(ChatModelViewHolder holder, String documentId, chatMesseageModel model) {
        BottomOptionDialogBinding bottomOptionDialogBinding =
                BottomOptionDialogBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(bottomOptionDialogBinding.getRoot());
        bottomOptionDialogBinding.deleteMessage.setOnClickListener(view ->
                firebaseUtil.getChatRoomReference(chatRoomID)
                        .collection("chats")
                        .document(documentId)
                        .update("message", "Tin nhắn đã bị thu hồi!").addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Toasty.success(context, "Thu hồi tin nhắn thành công!", Toasty.LENGTH_LONG, true).show();
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "onLongClick: " + e.getMessage());
                            Toasty.error(context, "Thu hồi tin nhắn thất bại!", Toasty.LENGTH_LONG, true).show();
                        }));

        bottomOptionDialogBinding.copyText.setOnClickListener(view -> {
            copyToClipboard(model);
            dialog.dismiss();
        });

        bottomOptionDialogBinding.cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    private void copyToClipboard(chatMesseageModel model){
        // Tạo ClipboardManager
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        // Tạo một đối tượng ClipData để chứa dữ liệu copy
        ClipData clip = ClipData.newPlainText("label", model.getMessage());

        // Sao chép dữ liệu vào Clipboard
        clipboard.setPrimaryClip(clip);
        Toasty.success(context, "Sao chép tin nhắn thành công!", Toasty.LENGTH_LONG, true).show();
    }


    private Typeface getTypeface(String type) {
        if (type.equals("RobotoBoldTextView")) return utils.getRobotoBoldTypeFace(context);
        else if (type.equals("RobotoItalicTextView")) return utils.getRobotoItalicTypeFace(context);
        else if (type.equals("BlackjackTextview")) return utils.getBlackjackTypeFace(context);
        else if (type.equals("AlluraTextView")) return utils.getAlluraTypeFace(context);
        else return utils.getRobotoLightTypeFace(context);
    }

}
