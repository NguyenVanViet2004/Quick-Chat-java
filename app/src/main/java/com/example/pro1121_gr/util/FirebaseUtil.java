package com.example.pro1121_gr.util;

public class FirebaseUtil {
    /*public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }*/

    /*public static boolean isLoggedIn(){
        return currentUserId() != null;
    }*/

   /* public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }*/

    /*public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }*/

    /*public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatroomId);
    }*/

   /* public static CollectionReference getChatroomMessageReference(String chatroomId){
        return ChatRoomsDAO.getChatRoomReference(chatroomId).collection("chats");
    }*/

    /*public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }*/

    /*public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }*/

    /*public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }*/

    /*public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }*/


    //Đăng xuất
    /*public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }*/

    /*public static StorageReference getCurrentProfileImageStorageReference(){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(FirebaseUtil.currentUserId());
    }*/

    /*public static StorageReference  getCurrentOtherProfileImageStorageReference(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(otherUserId);
    }*/

    /*public static void loadImageInChat(Context context, String message, ImageView image) {
        Glide.with(context)
                .load(message)
                .apply(new RequestOptions()
                        .override(500, 500) // Điều chỉnh kích thước ảnh
                        .fitCenter() // Giữ tỷ lệ và hiển thị trong khung
                )
                .into(image); // ImageView để hiển thị ảnh
    }*/

    /*public static void setAvatar(Context context, Uri uri, ImageView image) {
        Glide.with(context)
                .load(uri)
                .error(R.drawable.img_5) // Đặt hình ảnh mặc định khi có lỗi
                .apply(RequestOptions.circleCropTransform())
                .into(image);
    }*/


}
