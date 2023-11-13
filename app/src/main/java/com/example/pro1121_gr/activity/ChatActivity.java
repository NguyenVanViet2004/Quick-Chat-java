package com.example.pro1121_gr.activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pro1121_gr.adapter.chatAdapter;
import com.example.pro1121_gr.databinding.ActivityChatBinding;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.chatMesseageModel;
import com.example.pro1121_gr.model.chatRoomModel;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;


import java.util.Arrays;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private userModel userModel;
    String chatRoomID;

    private Uri uriOther;
    private chatRoomModel chatRoomModel;

    private chatAdapter adapter;

    private ActivityChatBinding binding;

    private final int REQUEST_IMAGE_PICK = 100;
    private final int REQUEST_IMAGE_CAPTURE = 1000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get UserModel
        userModel = StaticFunction.getUserModelFromIntent(getIntent());
        chatRoomID = firebaseUtil.getChatroomId(firebaseUtil.currentUserId(), userModel.getUserId());

        binding.backFragmentMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getDataChatRoom();

        binding.usernameMess.setText(userModel.getUsername());

        binding.TextMESS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()){
                    binding.sendMess.setVisibility(View.VISIBLE);
                    binding.like.setVisibility(View.GONE);
                }else{
                    binding.sendMess.setVisibility(View.GONE);
                    binding.like.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.TextMESS.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    hiddenItem(true);
                }
                return false;
            }
        });

        binding.rcvMess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    hiddenItem(false);
                    if (binding.TextMESS.getText().toString().isEmpty()){
                        binding.sendMess.setVisibility(View.GONE);
                        binding.like.setVisibility(View.VISIBLE);
                    }
                    binding.TextMESS.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(binding.TextMESS.getWindowToken(), 0); // Ẩn bàn phím ảo (nếu đang hiển thị)
                }
                return false;
            }
        });

        binding.sendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hiddenItem(false);
                sendMessToOther(binding.TextMESS.getText().toString().trim());
            }
        });
        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();


    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessToOther(String message) {
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(firebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);

        firebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);

        chatMesseageModel chatMesseageModel = new chatMesseageModel(message,firebaseUtil.currentUserId(),Timestamp.now());

        /*firebaseUtil.getChatroomMessageReference(chatRoomID).add(chatMesseageModel).addOnCompleteListener( task -> {
            if (task.isSuccessful()){
            }
        });*/
        firebaseUtil.getChatroomMessageReference(chatRoomID).add(chatMesseageModel);
        binding.TextMESS.setText("");
        adapter.notifyDataSetChanged();
    }

    private void hiddenItem(boolean value) {
        if (value){
            binding.imageMess.setVisibility(View.GONE);
            binding.cameraMess.setVisibility(View.GONE);
        }else {
            binding.imageMess.setVisibility(View.VISIBLE);
            binding.cameraMess.setVisibility(View.VISIBLE);
        }
    }

    private void getDataChatRoom() {
        try {
            // Lấy đối tượng userModel
            userModel = StaticFunction.getUserModelFromIntent(getIntent());

            chatRoomID = firebaseUtil.getChatroomId(firebaseUtil.currentUserId().toString(), userModel.getUserId());

            // Lấy avatar
            firebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        uriOther = uri;
                        firebaseUtil.setAvatar(ChatActivity.this, uri, binding.avatarChat);
                        setChatLayout();
                    }
                }
            });
            setChatLayout();
            firebaseUtil.getChatRoomReference(chatRoomID).get().addOnCompleteListener(task ->  {
                chatRoomModel = task.getResult().toObject(chatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new chatRoomModel();
                    chatRoomModel.setChatroomId(chatRoomID);
                    chatRoomModel.setUserIds(Arrays.asList(firebaseUtil.currentUserId(), userModel.getUserId()));
                    chatRoomModel.setLastMessageTimestamp(Timestamp.now());
                    chatRoomModel.setLastMessageSenderId("");
                }
                firebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);

            });
        } catch (Exception e) {
            Log.e("getDataChatRoom", e.toString() );
        }

    }

    private void setChatLayout() {
        try {
            Query query = firebaseUtil.getChatroomMessageReference(chatRoomID)
                    .orderBy("timestamp", Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<chatMesseageModel> options = new FirestoreRecyclerOptions.Builder<chatMesseageModel>()
                    .setQuery(query, chatMesseageModel.class)
                    .build();

            adapter = new chatAdapter(options, getApplicationContext(), uriOther);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setReverseLayout(true);
            binding.rcvMess.setLayoutManager(manager);
            binding.rcvMess.setAdapter(adapter);
            adapter.startListening();

            // Cuộn màn hình xuống tin nhắn mới nhất khi gửi
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    binding.rcvMess.smoothScrollToPosition(0);
                }
            });

        } catch (Exception e) {
            Log.e("SearchUser.kt", Objects.requireNonNull(e.getMessage()));
        }
    }

}