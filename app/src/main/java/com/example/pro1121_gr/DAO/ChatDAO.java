package com.example.pro1121_gr.DAO;

import com.google.firebase.firestore.CollectionReference;

public class ChatDAO {
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return ChatRoomsDAO.getChatRoomReference(chatroomId).collection("chats");
    }
}
