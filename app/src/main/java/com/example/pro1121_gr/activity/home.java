package com.example.pro1121_gr.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityHomeBinding;
import com.example.pro1121_gr.fragments.ChatFragment;
import com.example.pro1121_gr.function.ReplaceFragment;
import com.example.pro1121_gr.function.RequestPermission;
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class home extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ReplaceFragment.replaceFragment(
                this.getSupportFragmentManager(),
                R.id.frame_layout,
                new ChatFragment(),
                false
        );

        RequestPermission.requestReadImgGalleryCamera(this);

        binding.bottomNavigation.add(new CurvedBottomNavigation.Model(1, "Tin nhắn", R.drawable.baseline_message_24));
        binding.bottomNavigation.add(new CurvedBottomNavigation.Model(2, "Thêm", R.drawable.ic_baseline_add_24));
        binding.bottomNavigation.add(new CurvedBottomNavigation.Model(3, "cài đặt", R.drawable.baseline_settings_24));


        binding.bottomNavigation.setOnClickMenuListener(new Function1<CurvedBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(CurvedBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        ReplaceFragment.replaceFragment(getSupportFragmentManager(), R.id.frame_layout, new ChatFragment(), true);
                        break;
                    case 2:
                        showBottomDialog();
                        break;
                    case 3:
                        startActivity(new Intent(home.this, SettingActivity.class));
                        break;
                }
                return null;
            }
        });


    }

    private void showBottomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
        }
    }
}