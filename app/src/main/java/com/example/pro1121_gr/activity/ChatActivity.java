package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}