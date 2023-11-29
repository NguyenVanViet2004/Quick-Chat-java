package com.example.pro1121_gr.function;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    private static MyApplication instance;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String NIGHT_MODE = "NightMode";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applyNightMode();
    }

    public static MyApplication getInstance() {
        return instance;
    }
    // Áp dụng chế độ tối dựa trên sở thích của người dùng
    public static void applyNightMode() {
        //Lấy chế độ tối đã được lưu
        int mode = getSavedNightMode();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static void toggleNightMode() {
        // Chuyển đổi giữa chế độ tối và sáng
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Lưu trạng thái chế độ tối mới
        saveNightModeState(currentMode != AppCompatDelegate.MODE_NIGHT_YES);
    }

    private static int getSavedNightMode() {
        SharedPreferences prefs = getInstance().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }


    //lưu trạng thái chế độ tối vào SharedPreferences
    private static void saveNightModeState(boolean isNightModeOn) {
        SharedPreferences.Editor editor = getInstance().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(NIGHT_MODE, isNightModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        editor.apply();
    }

}
