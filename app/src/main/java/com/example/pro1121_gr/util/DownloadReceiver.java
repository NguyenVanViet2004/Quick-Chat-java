package com.example.pro1121_gr.util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.function.RequestPermission;

public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (downloadId != -1) {
            // Kiểm tra trạng thái tải về
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            try (Cursor cursor = manager.query(query)) {
                if (cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // Ảnh đã tải về thành công
                        Functions.Toasty(context, context.getString(R.string.download_successfully), Functions.success);
                    } else {
                        // Lỗi khi tải về ảnh
                        Functions.Toasty(context, context.getString(R.string.download_failed), Functions.error);
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public static void progressDownload(String uri, Activity activity, int REQUEST_WRITE_EXTERNAL_STORAGE){
        if (RequestPermission.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Uri link = Uri.parse(uri);
            DownloadManager.Request request = new DownloadManager.Request(link);
            request.setTitle("Download");
            request.setDescription("Downloading");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg");

            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

            long downloadId = manager.enqueue(request);

            // Gửi một Intent để thông báo khi tải về hoàn tất
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            activity.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (completedDownloadId == downloadId) {
                        // Gửi một Intent với hành động tải về đã hoàn tất
                        Intent downloadCompleteIntent = new Intent("DOWNLOAD_COMPLETE");
                        activity.sendBroadcast(downloadCompleteIntent);
                    }
                }
            }, filter);
        } else RequestPermission.requestWriteExternalStoragePermission(activity, REQUEST_WRITE_EXTERNAL_STORAGE);
    }

}
