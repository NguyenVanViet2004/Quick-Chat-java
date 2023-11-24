package com.example.pro1121_gr.util;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.function.RequestPermission;

import es.dmoral.toasty.Toasty;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private boolean isFirstLogin = true;
    private Dialog isDialog;
    @Override
    public void onReceive(Context context, Intent intent) {

        // kiểm tra lắng nghe được sự thay đổi network hay chưa
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            // Kiểm tra trạng thái mạng
            if (isNetworkConnected(context)) {
                if (isFirstLogin) {
                    // Nếu là lần đầu tiên đăng nhập, không hiển thị thông báo
                    isFirstLogin = false;
                } else {
                    // Hiển thị thông báo khi có kết nối mạng trở lại
                    Toasty.success(context, context.getString(R.string.network_comeback), Toast.LENGTH_SHORT, true).show();
                    if (isDialog != null) isDialog.dismiss();
                }
            } else {
                // Kết nối mạng không ổn định hoặc không có kết nối mạng
                showError((Activity) context);
                //Toasty.error(context, R.string.error_network, Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    // Hàm kiểm tra kết nối mạng
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Sử dụng ConnectivityManager.NetworkCallback cho API level >= 23
                Network activeNetwork = connectivityManager.getActiveNetwork();
                return activeNetwork != null;
            } else {
                // Sử dụng NetworkInfo cho API level < 23
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    private void showError(Activity activity){
        android.view.LayoutInflater inflater = activity.getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.error_dialog, null);

        // set dialog
        Dialog builder = new Dialog(activity);
        builder.setContentView(dialogView);
        builder.setCancelable(false);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.show();
        isDialog = builder;

        Button button = dialogView.findViewById(R.id.errorClose);
        button.setOnClickListener(view -> {
            builder.dismiss();
            activity.finish();
        });

        Button setting = dialogView.findViewById(R.id.errorGoToSetting);
        setting.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
