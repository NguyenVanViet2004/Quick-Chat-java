package com.example.pro1121_gr.activity;


import android.annotation.SuppressLint;
import android.net.Uri;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = ChatActivity.class.getSimpleName();


    private userModel userModel;
    String chatRoomID;

    private String uriOther;
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
        Log.e(TAG, "onCreate: " + userModel.getFMCToken());
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

        firebaseUtil.getChatroomMessageReference(chatRoomID).add(chatMesseageModel);
        sentotification(message);
        binding.TextMESS.setText("");
        //adapter.notifyDataSetChanged();
    }

    private void sentotification(String message) {
        firebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userModel userModel1 = task.getResult().toObject(userModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("title", userModel1.getUsername());
                    notification.put("body", message);
                    JSONObject data = new JSONObject();
                    data.put("userId", userModel1.getUserId());
                    jsonObject.put("notification", notification);
                    jsonObject.put("data", data);
                    jsonObject.put("to", userModel.getFMCToken());
                    callAPI(jsonObject);
                    Log.e(TAG, "my token222: " + userModel.getFMCToken() );

                } catch (Exception e) {
                    Log.e(ChatActivity.class.getSimpleName(), "notification: " + e.getMessage());
                }
            }
        });
    }


    private void callAPI(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder().url(url).post(body).header("Authorization", "Bearer AAAAzn2wU_4:APA91bGbYkKRd6E_tfTinPa_aBOnjjYLU39FJFubqWUUGOzV9uOEffB0gz_auOKjeJCqW7trohwb2aTSMFPcVVlTKD4NivbaubTDaKsHaWH3UMAom6pac6bMLfOA7ZhBQ1T1z1Tj1_vJ")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(ChatActivity.class.getSimpleName(), "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(ChatActivity.class.getSimpleName(), "onResponse: " + response);
            }
        });
    }

    private void hiddenItem(boolean value) {
        if (value) {
            binding.imageMess.setVisibility(View.GONE);
            binding.cameraMess.setVisibility(View.GONE);
        } else {
            binding.imageMess.setVisibility(View.VISIBLE);
            binding.cameraMess.setVisibility(View.VISIBLE);
        }
    }

    private void getDataChatRoom() {
        try {
            // Lấy đối tượng userModel
            userModel = StaticFunction.getUserModelFromIntent(getIntent());

            chatRoomID = firebaseUtil.getChatroomId(firebaseUtil.currentUserId(), userModel.getUserId());

            // Lấy avatar
            firebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        uriOther = uri.toString();
                        Log.e(TAG, "getdata: "+ uriOther );
                        firebaseUtil.setAvatar(ChatActivity.this, uri, binding.avatarChat);
                        setChatLayout();
                    }
                }
            });
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
        //try {
        Query query = firebaseUtil.getChatroomMessageReference(chatRoomID)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatMesseageModel> options = new FirestoreRecyclerOptions.Builder<chatMesseageModel>()
                .setQuery(query, chatMesseageModel.class)
                .build();

        adapter = new chatAdapter(options, getApplicationContext(), uriOther.toString());
        Log.e(TAG, "onComplete: " + uriOther.toString());
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

        /*} catch (Exception e) {
            Log.e(TAG, "setChatLayout : "+e.getMessage());
        }*/
    }

}