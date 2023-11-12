package com.example.pro1121_gr.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.example.pro1121_gr.R;

import es.dmoral.toasty.Toasty;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private boolean isFirstLogin = true;
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
                }
            } else {
                // Kết nối mạng không ổn định hoặc không có kết nối mạng
                Toasty.error(context, R.string.error_network, Toast.LENGTH_SHORT, true).show();
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
}
