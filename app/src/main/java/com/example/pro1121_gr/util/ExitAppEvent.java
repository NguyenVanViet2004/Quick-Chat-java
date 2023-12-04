package com.example.pro1121_gr.util;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;

public class ExitAppEvent extends Service {
    private static final String TAG = "MyBackgroundService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service destroyed");
        stopService();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "onTaskRemoved: " );
        stopService();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory: " );
        stopService();
    }

    private void stopService(){
        if (UserDAO.currentUserId() != null && !UserDAO.currentUserId().equals("")){
            DBhelper.getInstance(this).endUsageTracking();
            UserDAO.setOffline();
        }
        stopSelf();
    }
}
