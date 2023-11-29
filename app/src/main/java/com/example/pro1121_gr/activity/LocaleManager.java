package com.example.pro1121_gr.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleManager {

    private static final String LANGUAGE_KEY = "language_key";
    private static final String DEFAULT_LANGUAGE = "vi"; // Ngôn ngữ mặc định

    public static void setLocale(Context context, String languageCode) {
        Resources res = context.getResources();
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Lưu ngôn ngữ đã chọn vào SharedPreferences
        saveLanguagePreference(context, languageCode);
    }

    public static void saveLanguagePreference(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
    }


}

