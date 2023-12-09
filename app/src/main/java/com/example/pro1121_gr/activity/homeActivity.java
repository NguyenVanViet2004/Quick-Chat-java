package com.example.pro1121_gr.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pro1121_gr.DAO.ChatRoomsDAO;
import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.DesignPattern.UserSingleton;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.adapter.ChatListAvatarAdapter;
import com.example.pro1121_gr.adapter.chatListAdapter;
import com.example.pro1121_gr.databinding.ActivityHomeBinding;
import com.example.pro1121_gr.function.MyApplication;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class homeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean doubleBackToExitPressedOnce = false;
    private chatListAdapter adapter;
    private ChatListAvatarAdapter listAvatarAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getValueSharedPreferences();
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Bật chế độ tối nếu được kích hoạt
        UserSingleton.getInstance().getData(this);
        MyApplication.applyNightMode();
        getFMCtoken();
        initView();
    }

    private void getValueSharedPreferences(){
        SharedPreferences preferences = getSharedPreferences("language", MODE_PRIVATE);
        boolean vn = preferences.getBoolean("isVietnamese", false);
        boolean en = preferences.getBoolean("isEnglish", false);
        boolean ge = preferences.getBoolean("isGermany", false);
        if (vn) setLocale("vi");
        else if (en) setLocale("en");
        else if (ge) setLocale("de");
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void initView() {
        setSatus();
        setupClickEvents();
        setLayoutChat();
        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
    }

    private void setupClickEvents() {
        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(homeActivity.this, SearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setLayoutChat();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.setting.setOnClickListener(view -> {
            startActivity(new Intent(homeActivity.this,SettingActivity.class));
        });
    }

    private void setLayoutChat() {
        Query query = ChatRoomsDAO.allChatroomCollectionReference()
                .whereArrayContains("userIds", UserDAO.currentUserId())
                .orderBy("lastMessageSenderId", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatRoomModel> options =
                new FirestoreRecyclerOptions.Builder<chatRoomModel>().setQuery(query, chatRoomModel.class).build();

        adapter = new chatListAdapter(options,homeActivity.this);
        listAvatarAdapter = new ChatListAvatarAdapter(options, homeActivity.this);

        binding.rcvListChat.setLayoutManager(new LinearLayoutManager(homeActivity.this));
        binding.rcvListChat.setAdapter(adapter);
        binding.rcvListAvt.setLayoutManager(new LinearLayoutManager(homeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.rcvListAvt.setAdapter(listAvatarAdapter);
        adapter.startListening();
        listAvatarAdapter.startListening();
    }

    private void setSatus(){
        UserDAO.currentUserDetails().update("status",1);
    }

    private void getFMCtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.e(homeActivity.class.getSimpleName(), "getFMCtoken: " + task.getResult() );
            UserDAO.currentUserDetails().update("fmctoken",task.getResult());
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null) adapter.startListening();
        if (listAvatarAdapter != null) listAvatarAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null) adapter.stopListening();
        if (listAvatarAdapter != null) listAvatarAdapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null) adapter.notifyDataSetChanged();
        if (listAvatarAdapter != null) listAvatarAdapter.notifyDataSetChanged();
    }


    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        //DBhelper.getInstance(this).endUsageTracking();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            DBhelper.getInstance(this).endUsageTracking();
            UserDAO.setOffline();
            return;
        }
        Toasty.warning(this, R.string.double_back_To_exit_pressed_once, Toasty.LENGTH_LONG, true).show();
        this.doubleBackToExitPressedOnce = true;


        // Đặt thời gian chờ để reset trạng thái doubleBackToExitPressedOnce
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                },
                2000 // 2 giây
        );
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any necessary data here, including language settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("lang", getCurrentLanguage()).apply();
    }

    private String getCurrentLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("lang", "vi");
    }*/
}