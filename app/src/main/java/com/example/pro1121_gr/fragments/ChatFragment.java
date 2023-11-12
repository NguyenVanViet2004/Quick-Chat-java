package com.example.pro1121_gr.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pro1121_gr.activity.MyApplication;
import com.example.pro1121_gr.activity.SearchActivity;
import com.example.pro1121_gr.adapter.chatListAdapter;
import com.example.pro1121_gr.databinding.FragmentChatBinding;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    private chatListAdapter adapter;

    public ChatFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(getLayoutInflater());
        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        setLayoutChat();
        MyApplication.applyNightMode();


        return binding.getRoot();
    }

    private void setLayoutChat() {
        Query query = firebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", firebaseUtil.currentUserId())
                .orderBy("lastMessageSenderId", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatRoomModel> options =
                new FirestoreRecyclerOptions.Builder<chatRoomModel>().setQuery(query, chatRoomModel.class).build();

        adapter = new chatListAdapter(options,getContext());

        binding.rcvListChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvListChat.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

}