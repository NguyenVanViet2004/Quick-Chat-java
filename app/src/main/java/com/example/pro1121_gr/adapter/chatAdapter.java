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

import com.example.pro1121_gr.DAO.ChatDAO;
import com.example.pro1121_gr.DAO.ChatRoomsDAO;
import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.custom_textview.Utils;
import com.example.pro1121_gr.databinding.BottomOptionDialogBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.CustomTypefaceInfo;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import es.dmoral.toasty.Toasty;

public class chatAdapter extends FirestoreRecyclerAdapter<chatMesseageModel, chatAdapter.ChatModelViewHolder> {

    private final Context context;
    private final String uriOther;
    private final String chatRoomID;
    private static final String TAG = chatAdapter.ChatModelViewHolder.class.toString();

    private Download download;




    public chatAdapter(@NonNull FirestoreRecyclerOptions<chatMesseageModel> options, Context context, String uriOther, String ChatRoomId, Download download) {
        super(options);
        this.context = context;
        this.uriOther = uriOther;
        this.chatRoomID = ChatRoomId;
        this.download = download;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull chatMesseageModel model) {
        String documentId = getSnapshots().getSnapshot(position).getId();
        if (model.getSenderId().equals(UserDAO.currentUserId()))
            setChatRightLayout(holder, model, documentId);
            // xử lý giao diện chat của đối phương
        else setChatLeftLayout(holder, model);

        holder.otherSendImg.setOnClickListener(view -> download.clickImage(model.getMessage()));
        holder.mySendImg.setOnClickListener(view -> download.clickImage(model.getMessage()));
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

        ImageView otherAVT, mySendImg, otherSendImg;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            otherAVT = itemView.findViewById(R.id.item_avatar);
            mySendImg = itemView.findViewById(R.id.mySendImg);
            otherSendImg = itemView.findViewById(R.id.otherSendImg);

        }
    }

    /*private void setChatRightLayout(ChatModelViewHolder holder, chatMesseageModel model, String documentId) {
        if (Functions.isURL(model.getMessage())){
            holder.mySendImg.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setVisibility(View.GONE);
            // Sử dụng Glide để hiển thị ảnh từ URL vào ImageView
            FirebaseUtil.loadImageInChat(context, model.getMessage(), holder.mySendImg);
        }else{
            holder.rightChatTextview.setText(model.getMessage());
            holder.mySendImg.setVisibility(View.GONE);
        }
        if (model.getTypeface() != null) {
            Utils.setFontForTextView(holder.rightChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        } else model.setTypeface(new CustomTypefaceInfo("RobotoLightTextView"));
        holder.leftChatLayout.setVisibility(View.GONE);
        holder.otherAVT.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);

        holder.rightChatTextview.setOnLongClickListener(view -> {
            showBottomDialog(holder, documentId, model, false);
            return true;
        });

        holder.mySendImg.setOnLongClickListener(view -> {showBottomDialog(holder, documentId, model, false); return true;});
    }

    private void setChatLeftLayout(ChatModelViewHolder holder, chatMesseageModel model) {
        FirebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUtil.setAvatar(context, Uri.parse(uriOther), holder.otherAVT);
            } else {
                Log.e(TAG, "Download URL not successful");
            }
        });

        if (Functions.isURL(model.getMessage())){
            holder.otherSendImg.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setVisibility(View.GONE);
            holder.otherAVT.setVisibility(View.VISIBLE);
            // Sử dụng Glide để hiển thị ảnh từ URL vào ImageView
            FirebaseUtil.loadImageInChat(context, model.getMessage(), holder.otherSendImg);
        }else {
            holder.leftChatTextview.setText(model.getMessage());
            holder.otherSendImg.setVisibility(View.GONE);
            holder.otherAVT.setVisibility(View.VISIBLE);
        }
        if (model.getTypeface() != null) {
            Utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        } else model.setTypeface(new CustomTypefaceInfo("RobotoLightTextView"));
        //utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));
        holder.rightChatLayout.setVisibility(View.GONE);
        holder.leftChatLayout.setVisibility(View.VISIBLE);

        holder.leftChatTextview.setOnLongClickListener(view -> {
            copyToClipboard(model);
            return true;
        });

        holder.otherSendImg.setOnLongClickListener(view -> {
            showBottomDialog(holder, "", model, true);
            return true;
        });

    }*/

    private void setChatRightLayout(ChatModelViewHolder holder, chatMesseageModel model, String documentId) {
        boolean isUrl = Functions.isURL(model.getMessage());
        holder.mySendImg.setVisibility(isUrl ? View.VISIBLE : View.GONE);
        holder.rightChatTextview.setVisibility(isUrl ? View.GONE : View.VISIBLE);
        if (isUrl) {
            ChatDAO.loadImageInChat(context, model.getMessage(), holder.mySendImg);
        } else {
            holder.rightChatTextview.setText(model.getMessage());
        }

        model.setTypeface(model.getTypeface() != null ? model.getTypeface() : new CustomTypefaceInfo("RobotoLightTextView"));
        Utils.setFontForTextView(holder.rightChatTextview, getTypeface(model.getTypeface().getTypefaceName()));

        holder.leftChatLayout.setVisibility(View.GONE);
        holder.otherAVT.setVisibility(View.GONE);
        holder.rightChatLayout.setVisibility(View.VISIBLE);

        holder.rightChatTextview.setOnLongClickListener(view -> {
            showBottomDialog(holder, documentId, model, false);
            return true;
        });

        holder.mySendImg.setOnLongClickListener(view -> {
            showBottomDialog(holder, documentId, model, false);
            return true;
        });
    }

    private void setChatLeftLayout(ChatModelViewHolder holder, chatMesseageModel model) {
        UserDAO.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserDAO.setAvatar(context, Uri.parse(uriOther), holder.otherAVT);
            } else {
                Log.e(TAG, "Download URL not successful");
            }
        });

        boolean isUrl = Functions.isURL(model.getMessage());
        holder.otherSendImg.setVisibility(isUrl ? View.VISIBLE : View.GONE);
        holder.leftChatTextview.setVisibility(isUrl ? View.GONE : View.VISIBLE);
        holder.otherAVT.setVisibility(View.VISIBLE);

        if (isUrl) {
            ChatDAO.loadImageInChat(context, model.getMessage(), holder.otherSendImg);
        } else {
            holder.leftChatTextview.setText(model.getMessage());
        }

        model.setTypeface(model.getTypeface() != null ? model.getTypeface() : new CustomTypefaceInfo("RobotoLightTextView"));
        Utils.setFontForTextView(holder.leftChatTextview, getTypeface(model.getTypeface().getTypefaceName()));

        holder.rightChatLayout.setVisibility(View.GONE);
        holder.leftChatLayout.setVisibility(View.VISIBLE);

        holder.leftChatTextview.setOnLongClickListener(view -> {
            copyToClipboard(model);
            return true;
        });

        holder.otherSendImg.setOnLongClickListener(view -> {
            showBottomDialog(holder, "", model, true);
            return true;
        });
    }



    private void showBottomDialog(ChatModelViewHolder holder, String documentId, chatMesseageModel model, boolean recallMessage) {
        BottomOptionDialogBinding bottomOptionDialogBinding =
                BottomOptionDialogBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(bottomOptionDialogBinding.getRoot());
        if (recallMessage) {
            bottomOptionDialogBinding.copyText.setVisibility(View.GONE);
            bottomOptionDialogBinding.deleteMessage.setVisibility(View.GONE);
        } else {
            bottomOptionDialogBinding.deleteMessage.setVisibility(View.VISIBLE);
            bottomOptionDialogBinding.copyText.setVisibility(View.VISIBLE);
        }
        bottomOptionDialogBinding.deleteMessage.setOnClickListener(view ->
                ChatRoomsDAO.getChatRoomReference(chatRoomID)
                        .collection("chats")
                        .document(documentId)
                        .update("message", R.string.recall_message).addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Toasty.success(context, R.string.recall_succ, Toasty.LENGTH_LONG, true).show();
                        }).addOnFailureListener(e -> {
                            Toasty.error(context, R.string.recall_failed, Toasty.LENGTH_LONG, true).show();
                        }));

        bottomOptionDialogBinding.copyText.setOnClickListener(view -> {
            copyToClipboard(model);
            dialog.dismiss();
        });

        bottomOptionDialogBinding.downloadImg.setOnClickListener(view -> {
            download.downloadImage(model.getMessage());
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
        switch (type) {
            case "RobotoBoldTextView":
                return Utils.getRobotoBoldTypeFace(context);
            case "RobotoItalicTextView":
                return Utils.getRobotoItalicTypeFace(context);
            case "BlackjackTextview":
                return Utils.getBlackjackTypeFace(context);
            case "AlluraTextView":
                return Utils.getAlluraTypeFace(context);
            default:
                return Utils.getRobotoLightTypeFace(context);
        }
    }

    public interface Download{
        void downloadImage(String uri);
        void clickImage(String model);
    }

}
