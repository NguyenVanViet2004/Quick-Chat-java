package com.example.pro1121_gr.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pro1121_gr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class firebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(firebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }


    //Đăng xuất
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfileImageStorageReference(){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(firebaseUtil.currentUserId());
    }

    public static StorageReference  getCurrentOtherProfileImageStorageReference(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(otherUserId);
    }

    public static void loadImageInChat(Context context, String message, ImageView image){
        Glide.with(context)
                .load(message)
                .override(500, 500) // Điều chỉnh kích thước ảnh tại đây
                .centerCrop() // Đường dẫn URL của ảnh đã tải lên
                .into(image); // ImageView để hiển thị ảnh
    }

    public static void setAvatar(Context context, Uri uri, ImageView image) {
        Glide.with(context)
                .load(uri)
                .apply(new RequestOptions()
                        .override(500, 500) // Điều chỉnh kích thước ảnh tại đây
                        .fitCenter() // Giữ tỷ lệ và hiển thị trong khung
                )
                .into(image); // ImageView để hiển thị ảnh

    }

    public static void setAVTinChat(Context context,Uri uri, ImageView imageView){
        firebaseUtil.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri uri = null;
                if (task.isSuccessful()) {
                    uri = task.getResult();
                    firebaseUtil.setAvatar(context, uri, imageView);
                } else {
                    Log.e("set avatar", "Download URL not successful");
                }
            }
        });
    }


}
