package com.example.pro1121_gr.DesignPattern;

import android.app.Activity;
import android.net.Uri;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;

public class UserSingleton {
    private static UserSingleton instance;
    private static userModel user = new userModel();
    private static Uri urlAVT = null;

    public static UserSingleton getInstance(){
        if (instance == null){
            instance = new UserSingleton();
        }
        return instance;
    }

    public void getData(Activity activity) {
        UserDAO.currentUserDetails().get().addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()){
                user = task.getResult().toObject(userModel.class);
                if (user != null){
                    UserDAO.getCurrentOtherProfileImageStorageReference(user.getUserId())
                            .getDownloadUrl().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful())
                                    urlAVT = task1.getResult();
                            });
                    Functions.startService(user.getUserId(), user.getUsername(), activity);
                } else {
                    Functions.Toasty(activity,activity.getString(R.string.error), Functions.error);
                }
            }
        });
    }

    public userModel getUser() {
        return user;
    }

    public Uri getUrlAVT(){
        return urlAVT;
    }
}
