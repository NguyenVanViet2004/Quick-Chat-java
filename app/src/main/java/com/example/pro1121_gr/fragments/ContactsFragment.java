package com.example.pro1121_gr.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.activity.SearchActivity;
import com.example.pro1121_gr.adapter.searchUserAdapter;
import com.example.pro1121_gr.databinding.FragmentContactsBinding;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ContactsFragment extends Fragment {
    private FragmentContactsBinding binding;
    private searchUserAdapter adapter;

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        setupSearchRecyclerView("");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        binding.searchView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), SearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
//            }
//        });
        // Rest of your code
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupSearchRecyclerView(String searchName) {
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchName)
                .whereLessThanOrEqualTo("username", searchName + '\uf8ff');
        FirestoreRecyclerOptions<userModel> options = new FirestoreRecyclerOptions.Builder<userModel>()
                .setQuery(query, userModel.class)
                .build();
        adapter = new searchUserAdapter(options, requireActivity().getApplication());
        binding.rcvSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
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
}