package com.example.pro1121_gr.function;


import android.app.Activity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StaticFunction {

    public static void passUserModelAsIntent(Intent intent, userModel model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("FCMtoken",model.getFMCToken());
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



}
