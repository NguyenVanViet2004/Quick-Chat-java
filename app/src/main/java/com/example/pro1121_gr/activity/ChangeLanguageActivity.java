package com.example.pro1121_gr.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityChangeLanguageBinding;
import com.example.pro1121_gr.function.Functions;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    public ActivityChangeLanguageBinding binding;
    private SharedPreferences sharedPreferences;
    private final static String TAG = ChangeLanguageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        setupClickEvents();
    }

    private void initView() {
        sharedPreferences = getSharedPreferences("language", Context.MODE_PRIVATE);

        // Load saved language state
        boolean isEnglish = sharedPreferences.getBoolean("isEnglish", false);
        boolean isGermany = sharedPreferences.getBoolean("isGermany", false);
        binding.Switch2.setChecked(isEnglish);
        binding.Switch3.setChecked(isGermany);

        // Load Vietnamese language state
        boolean isVietnamese = sharedPreferences.getBoolean("isVietnamese", false);
        binding.Switch.setChecked(isVietnamese);
        //
        if (!isEnglish && !isGermany && !isVietnamese){
            binding.Switch.setChecked(true);
            binding.Switch.setClickable(false);
        }
        setClickableForLanguageSwitch(isEnglish, isGermany, isVietnamese);
    }


    private void setupClickEvents(){
        binding.Switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleSwitchChecked("vi", binding.Switch2, binding.Switch3);
                setClickableAgain(binding.Switch, binding.Switch3, binding.Switch2);
            }
        });

        binding.Switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleSwitchChecked("en", binding.Switch, binding.Switch3);
                setClickableAgain(binding.Switch2, binding.Switch, binding.Switch3);
            }
        });

        binding.Switch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleSwitchChecked("de", binding.Switch, binding.Switch2);
                setClickableAgain(binding.Switch3, binding.Switch, binding.Switch2);
            }
        });

        binding.backSetting.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void handleSwitchChecked(String languageCode, Switch switch1, Switch switch2) {
        switch1.setChecked(false);
        switch2.setChecked(false);
        switch1.setClickable(true);
        switch2.setClickable(true);
        setLocale(languageCode);
    }

    private void setClickableForLanguageSwitch(boolean isEnglish, boolean isGermany, boolean isVietnamese) {
        if (isEnglish) {
            setClickable(binding.Switch2, binding.Switch, binding.Switch3);
        } else if (isGermany) {
            setClickable(binding.Switch3, binding.Switch2, binding.Switch);
        } else if (isVietnamese) {
            setClickable(binding.Switch, binding.Switch2, binding.Switch3);
        }
    }

    private void setClickable(Switch switchVietNamese, Switch switchEnglist, Switch switchGerman) {
        switchVietNamese.setClickable(false);
        switchEnglist.setClickable(true);
        switchGerman.setClickable(true);
    }

    private void setClickableAgain(Switch switchIsClick, Switch switch1, Switch switch2){
        switchIsClick.setClickable(false);
        switch1.setClickable(true);
        switch2.setClickable(true);
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

        showDialog();
    }

    private void showDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.change_language_titile).setMessage(R.string.change_language_message)
                .setPositiveButton(R.string.change_language_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restart();
                    }
                })
                .setNegativeButton(R.string.change_language_option2, null)
                .setIcon(R.drawable.icon_question).show();
    }


    private void restart() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Functions.showSnackBar(binding.getRoot(),getString(R.string.change_language_errorReload));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}