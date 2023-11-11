package com.example.pro1121_gr.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Xử lý sự kiện thay đổi trạng thái mạng ở đây
        // Kiểm tra trạng thái mạng và thực hiện hành động tương ứng
        if (isNetworkConnected(context)) {
            // Mạng đã kết nối
        } else {
            // Kết nối mạng không ổn định hoặc không có kết nối mạng
            Toast.makeText(context, "Kết nối mạng không ổn định hoặc không có kết nối internet. Vui lòng kiểm tra kết nối.", Toast.LENGTH_LONG).show();
        }
    }

    // Hàm kiểm tra kết nối mạng
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
