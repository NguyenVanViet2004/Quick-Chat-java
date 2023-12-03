package com.example.pro1121_gr.DAO;

import com.example.pro1121_gr.util.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserDAO {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfileImageStorageReference(){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference  getCurrentOtherProfileImageStorageReference(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_img")
                .child(otherUserId);
    }
}
