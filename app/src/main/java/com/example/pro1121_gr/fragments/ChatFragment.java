package com.example.pro1121_gr.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pro1121_gr.DAO.ChatRoomsDAO;
import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.function.MyApplication;
import com.example.pro1121_gr.activity.SearchActivity;
import com.example.pro1121_gr.adapter.ChatListAvatarAdapter;
import com.example.pro1121_gr.adapter.chatListAdapter;
import com.example.pro1121_gr.databinding.FragmentChatBinding;
import com.example.pro1121_gr.model.chatRoomModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    private chatListAdapter adapter;

    private ChatListAvatarAdapter listAvatarAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(getLayoutInflater());
        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setLayoutChat();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        setLayoutChat();
        MyApplication.applyNightMode();
        return binding.getRoot();
    }

    private void setLayoutChat() {
        Query query = ChatRoomsDAO.allChatroomCollectionReference()
                .whereArrayContains("userIds", UserDAO.currentUserId())
                .orderBy("lastMessageSenderId", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatRoomModel> options =
                new FirestoreRecyclerOptions.Builder<chatRoomModel>().setQuery(query, chatRoomModel.class).build();

        adapter = new chatListAdapter(options,getContext());
        listAvatarAdapter = new ChatListAvatarAdapter(options, getContext());

        binding.rcvListChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcvListChat.setAdapter(adapter);
        binding.rcvListAvt.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rcvListAvt.setAdapter(listAvatarAdapter);
        adapter.startListening();
        listAvatarAdapter.startListening();
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


}