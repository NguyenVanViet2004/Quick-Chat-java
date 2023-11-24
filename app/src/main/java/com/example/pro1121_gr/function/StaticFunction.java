package com.example.pro1121_gr.function;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.activity.CreateProfile;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.google.firebase.Timestamp;

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
                    if (!isValidDateFormat(String.valueOf(charSequence))) editText.setError("Sai định dạng Ngày-Tháng-Năm!");
                    else editText.setError(null);
                }else {
                    if (!isValidPhoneNumber(charSequence.toString())) editText.setError("Sai định dạng số điện thoại");
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
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Loại bỏ các ký tự không phải số khỏi chuỗi số điện thoại
        String cleanPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Kiểm tra xem số điện thoại có đúng định dạng không
        // 10 hoặc 11 chữ số (trong trường hợp số điện thoại quốc tế)
        return cleanPhoneNumber.matches("\\d{10,11}");
    }

    public static void showError(Activity activity){
        android.view.LayoutInflater inflater = activity.getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.error_dialog, null);

        // set dialog
        Dialog builder = new Dialog(activity);
        builder.setContentView(dialogView);
        builder.setCancelable(false);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.show();

        Button button = dialogView.findViewById(R.id.errorClose);
        button.setOnClickListener(view -> {
            builder.dismiss();
        });
    }


    public static void showWarning(Activity activity, String message){
        android.view.LayoutInflater inflater = activity.getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.waring_dialog, null);

        // set dialog
        Dialog builder = new Dialog(activity);
        builder.setContentView(dialogView);
        builder.setCancelable(false);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.show();

        TextView textView = dialogView.findViewById(R.id.errorDesc);
        Button button = dialogView.findViewById(R.id.warningClose);

        textView.setText(message);
        button.setOnClickListener(view -> {
            builder.dismiss();
        });
    }

    public static void openLink(Activity activity) {
        String facebookUri = "https://www.facebook.com/VietNguyenVan2004";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUri));

        if (intent.resolveActivity(activity.getPackageManager()) != null) activity.startActivity(intent);
        else activity.startActivity(intent);
    }



}
