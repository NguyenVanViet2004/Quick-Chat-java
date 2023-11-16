package com.example.pro1121_gr.function;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.example.pro1121_gr.R;

public class RequestPermission {

    @SuppressLint("ObsoleteSdkInt")
    public static boolean checkPermission(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestReadMediaImagesPermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, requestCode);
    }

    public static void requestWriteExternalStoragePermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode);
    }

    public static void requestReadExternalStoragePermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, requestCode);
    }

    public static void requestCameraPermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.CAMERA, requestCode);
    }

    public static void requestReadContactsPermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.READ_CONTACTS, requestCode);
    }

    public static void requestLocationPermission(Activity activity, int requestCode) {
        requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, requestCode);
    }

    private static void requestPermission(Activity activity, String permission, int requestCode) {
        if (!checkPermission(activity, permission)) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    public static void showPermissionRationaleDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Cảnh báo")
                .setMessage("Bạn chưa cấp đủ quyền cho ứng dụng, hãy vào cài đặt để cấp quyền.")
                .setIcon(R.drawable.baseline_warning_24)
                .setPositiveButton("Đến cài đặt", (dialog, which) -> openAppSettings(activity))
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
