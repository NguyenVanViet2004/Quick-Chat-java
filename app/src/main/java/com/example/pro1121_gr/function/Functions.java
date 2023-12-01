package com.example.pro1121_gr.function;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;


public class Functions {


    public static final int error = 0;
    public static final int success = 1;
    public static final int warning = 2;

    private static String idUser = null;

    public static String getIdUser() {
        if (idUser == null) idUser = FirebaseUtil.currentUserId();
        return idUser;
    }

    public static void setIdUser(String newId) {
        idUser = newId;
    }



    public static void passUserModelAsIntent(Intent intent, userModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("FCMtoken", model.getFMCToken());
    }

    public static userModel getUserModelFromIntent(Intent intent){
       userModel userModel = new userModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFMCToken(intent.getStringExtra("FCMtoken"));
        return userModel;
    }

    public static void setProfileImg(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
    public static boolean isURL(String input) {
        String urlPattern = "https?://.*";
        Pattern pattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static void isEmpty(EditText editText, int type){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (type == 0){
                    if (editText.getText().toString().trim().length() < 5) editText.setError("Tên người dùng không được nhỏ hơn 5 ký tự");
                    else editText.setError(null);
                } else if (type == 1) {
                    if (isValidDateFormat(String.valueOf(charSequence))) editText.setError("Sai định dạng Ngày-Tháng-Năm!");
                    else editText.setError(null);
                }else {
                    if (isValidPhoneNumber(charSequence.toString())) editText.setError("Sai định dạng số điện thoại");
                    else editText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public static boolean isValidDateFormat(String inputDate) {
        // Định dạng chuỗi ngày tháng dd-MM-yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        try {
            // Parse chuỗi thành đối tượng Date
            Date date = dateFormat.parse(inputDate);

            // Kiểm tra xem chuỗi có phù hợp với định dạng không
            return date == null;
        } catch (ParseException e) {
            return true;
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Loại bỏ các ký tự không phải số khỏi chuỗi số điện thoại
        String cleanPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Kiểm tra xem số điện thoại có đúng định dạng không
        // 10 hoặc 11 chữ số (trong trường hợp số điện thoại quốc tế)
        return !cleanPhoneNumber.matches("\\d{10,11}");
    }



    public static void openLink(Activity activity) {
        String facebookUri = "https://www.facebook.com/VietNguyenVan2004";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUri));

        if (intent.resolveActivity(activity.getPackageManager()) != null) activity.startActivity(intent);
        else activity.startActivity(intent);
    }

    public static void showSnackBar(View rootView, String message){
        Snackbar snackbar = Snackbar.make(
                rootView,
                message,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction("Cancel", view -> snackbar.dismiss());
        snackbar.show();
    }

    public static NetworkChangeReceiver getNetworkChangeReceiver(Activity activity){
        // Khởi tạo và đăng ký BroadcastReceiver
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(networkChangeReceiver, intentFilter);
        return networkChangeReceiver;
    }



    public static void startService(String userIdCall, String userName2, Activity activity){
        Application application = activity.getApplication(); // Android's application context
        long appID = 189168233;   // yourAppID
        String appSign = "57b5cefe54ad7738673c16a35e9b3a758bf7e116a0d6f9ee1b7ea1b7d1a8056e";  // yourAppSign
        String userID = userIdCall; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName = userName2;   // yourUserName


        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(activity.getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

    public static void setVideoCall(String targetUserID, String targetUserName, com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton videoCall) {
        videoCall.setIsVideoCall(true);
        videoCall.setResourceID("zego_uikit_call");
        videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }

    public static void setVoiceCall(String targetUserID, String targetUserName, com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton voiceCall) {
        voiceCall.setIsVideoCall(false);
        voiceCall.setResourceID("zego_uikit_call");
        voiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }


    public static void Toasty(Context context,String message, int type){
        switch (type){
            case 0: Toasty.error(context, message, Toasty.LENGTH_SHORT, true); break;
            case 1: Toasty.success(context, message, Toasty.LENGTH_SHORT, true); break;
            case 2: Toasty.warning(context, message, Toasty.LENGTH_SHORT, true); break;
        }
    }
}
