package com.example.pro1121_gr.DAO;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;

public class ChatDAO {
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return ChatRoomsDAO.getChatRoomReference(chatroomId).collection("chats");
    }

    public static void loadImageInChat(Context context, String message, ImageView image) {
        Glide.with(context)
                .load(message)
                .apply(new RequestOptions()
                        .override(500, 500) // Điều chỉnh kích thước ảnh
                        .fitCenter() // Giữ tỷ lệ và hiển thị trong khung
                )
                .into(image); // ImageView để hiển thị ảnh
    }
}
