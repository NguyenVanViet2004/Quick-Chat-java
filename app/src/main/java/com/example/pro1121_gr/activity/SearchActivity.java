package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.adapter.searchUserAdapter;
import com.example.pro1121_gr.databinding.ActivitySearchBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private searchUserAdapter adapter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);

        setupClickEvents();
        setupSearchRecyclerView("");

        MyApplication.applyNightMode();
    }

    private void setupClickEvents() {
        binding.backFragmentMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                setupSearchRecyclerView(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                setupSearchRecyclerView(s);
                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupSearchRecyclerView(String searchName) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchName)
                .whereLessThanOrEqualTo("username", searchName + '\uf8ff');
        FirestoreRecyclerOptions<userModel> options = new FirestoreRecyclerOptions.Builder<userModel>()
                .setQuery(query, userModel.class)
                .build();
        adapter = new searchUserAdapter(options, getApplication());
        binding.rcvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvSearch.setAdapter(adapter);
        // Thêm addOnCompleteListener để log số lượng item tìm thấy
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int itemCount = task.getResult().size();
                Log.e("SearchResult", "Số lượng item tìm thấy: " + itemCount);
            } else {
                Log.e("SearchResult", "Lỗi khi truy vấn: " + task.getException());
            }
        });
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        DBhelper.getInstance(this).endUsageTracking();
        FirebaseUtil.currentUserDetails().update("status",0);
    }
}