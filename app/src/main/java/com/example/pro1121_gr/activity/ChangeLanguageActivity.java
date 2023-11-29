package com.example.pro1121_gr.activity;

import static com.example.pro1121_gr.activity.LocaleManager.setLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityChangeLanguageBinding;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    public ActivityChangeLanguageBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);

// Load saved language state
        boolean isEnglish = sharedPreferences.getBoolean("isEnglish", false);
        boolean isGermany = sharedPreferences.getBoolean("isGermany", false);
        binding.Switch2.setChecked(isEnglish);
        binding.Switch3.setChecked(isGermany);

// Load Vietnamese language state
        boolean isVietnamese = sharedPreferences.getBoolean("isVietnamese", false);
        binding.Switch.setChecked(isVietnamese);


        binding.Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.Switch2.setChecked(false);
                    binding.Switch3.setChecked(false);
                    setLocale("vi"); // Vietnamese language code
                }
            }
        });

        binding.Switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.Switch.setChecked(false);
                    binding.Switch3.setChecked(false);
                    setLocale("en"); // English language code
                }
            }
        });

        binding.Switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.Switch.setChecked(false);
                    binding.Switch2.setChecked(false);
                    setLocale("de"); // German language code
                }
            }
        });

        binding.backSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangeLanguageActivity.this, SettingActivity.class));
            }
        });
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isEnglish", languageCode.equals("en"));
        editor.putBoolean("isGermany", languageCode.equals("de"));
        editor.putBoolean("isVietnamese", languageCode.equals("vi"));
        editor.apply();

        recreate();
    }
}